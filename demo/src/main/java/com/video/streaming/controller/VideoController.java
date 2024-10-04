package com.video.streaming.controller;

import com.video.streaming.AppConstants;
import com.video.streaming.entities.Video;
import com.video.streaming.payload.CustomMessage;
import com.video.streaming.services.VideoService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/videos")
@CrossOrigin("*")
public class VideoController {

  private VideoService videoService;

  @Value("${file.video.hsl}")
  private String HLS_DIR;

  @PostMapping
  public ResponseEntity<?> create(@RequestParam("file")MultipartFile file ,
                                              @RequestParam("title")String title ,
                                              @RequestParam("description")String description)
      throws IOException {

    Video video =
        Video.builder().videoId(UUID.randomUUID().toString()).
            title(title).description(description).build();
    Video savedVideo = videoService.save(video , file);
    if(savedVideo == null){
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
          (CustomMessage.builder().
              message("Failed to save video").
              success(false).
              build()));
    }
    return ResponseEntity.status(HttpStatus.OK).body(video);
  }


  // Problem with this method is you get whole video at once. Which can be a problem if the video
  // is large and you have limited memory.
  // Stream video
  @GetMapping("/stream/{videoId}")
  public ResponseEntity<Resource> stream(@PathVariable String videoId){
    Video video = videoService.get(videoId);

    String contentType = video.getContentType();
    String filePath = video.getFilePath();

    if(contentType == null) {
      contentType = "application/octet-stream";
    }

    Resource resource = new FileSystemResource(new File(filePath));

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(contentType))
        .body(resource);
  }

  // Stream video in chunks
  @GetMapping("/stream/range/{videoId}")
  public ResponseEntity<Resource> streamVideoRange(
      @PathVariable String videoId,
      @RequestHeader(value = "Range" , required = false) String range){
    System.out.println(range);
    Video video = videoService.get(videoId);
    Path path = Paths.get(video.getFilePath());

    Resource resource = new FileSystemResource(path);
    String contentType = video.getContentType();

    if(contentType == null){
      contentType = "application/octet-stream";
    }

    long fileLength = path.toFile().length(); // file size
    if(range == null){
      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(contentType))
          .body(resource);
    }

    long rangeStart;
    long rangeEnd;

    String[] ranges = range.replace("bytes=" , "").split("-");
    rangeStart = Long.parseLong(ranges[0]);


    // 1st way :
    // If end range is present
//    if(ranges.length > 1){
//      rangeEnd = Long.parseLong(ranges[1]);
//    } else{
//      // If end range is not present
//      rangeEnd = fileLength - 1;
//    }
//
//    if(rangeEnd > fileLength - 1){
//      rangeEnd = fileLength - 1;
//    }

    // 2nd way :
    rangeEnd = rangeStart + AppConstants.CHUNK_SIZE - 1;
    if(rangeEnd >= fileLength){
      rangeEnd = fileLength - 1;
    }

    InputStream inputStream;

    try{
      inputStream = Files.newInputStream(path);
      inputStream.skip(rangeStart);

      long contentLength = rangeEnd - rangeStart + 1;
      System.out.println("Range start = " + rangeStart);
      System.out.println("Range end = " + rangeEnd);
      System.out.println("Content length = " + contentLength);

      byte[] data = new byte[(int) contentLength];
      int read = inputStream.read(data , 0 , data.length);
      System.out.println("Read number of bytes = " + read);

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.add("Content-Range" , "bytes " + rangeStart + "-" +
          rangeEnd + "/" + fileLength);
      httpHeaders.setContentLength(contentLength);

      return ResponseEntity
          .status(HttpStatus.PARTIAL_CONTENT)
          .headers(httpHeaders)
          .contentType(MediaType.parseMediaType(contentType))
//          .body(new InputStreamResource(inputStream));
          .body(new ByteArrayResource(data));
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

  }

  @GetMapping
  public Iterable<Video> getAll(){
    return videoService.getAll();
  }

  // Get server hls playlist master.m3u8
  @GetMapping("/{videoId}/master.m3u8")
  public ResponseEntity<Resource> getHlsMasterPlaylist(@PathVariable String videoId){
    Path path = Paths.get(HLS_DIR , videoId , "master.m3u8");
    if(!Files.exists(path)){
      return ResponseEntity.notFound().build();
    }

    Resource resource = new FileSystemResource(path);

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType("application/vnd.apple.mpegurl"))
        .body(resource);
  }

  // Serve the segment
  @GetMapping("/{videoId}/{segment}.ts")
  public ResponseEntity<Resource> getHlsSegment(@PathVariable String videoId ,
                                               @PathVariable String segment){
    Path path = Paths.get(HLS_DIR , videoId , segment + ".ts");
    if(!Files.exists(path)){
      return ResponseEntity.notFound().build();
    }

    Resource resource = new FileSystemResource(path);

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType("video/mp2t"))
        .body(resource);
  }


}
