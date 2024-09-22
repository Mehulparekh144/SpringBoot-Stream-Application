package com.video.streaming.services.impl;

import com.video.streaming.entities.Video;
import com.video.streaming.repository.VideoRepository;
import com.video.streaming.services.VideoService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

  private final VideoRepository repository;
  private final VideoRepository videoRepository;

  @Value("${files.dir}")
  private String DIR;

  // This method will run after the bean is created
  @PostConstruct
  public void init(){
    File file = new File(DIR);
    if(file.mkdir()){
      System.out.println("Folder created = " + file.getAbsolutePath());
    } else{
      System.out.println("Folder already exists = " + file.getAbsolutePath());
    }
  }

  @Override
  public Video save(Video video, MultipartFile file) throws IOException {

    try{
      String fileName = file.getOriginalFilename();
      String contentType = file.getContentType();
      InputStream inputStream = file.getInputStream();

      // folder path -> create
      String cleanPath = StringUtils.cleanPath(fileName);
      String cleanFolder = StringUtils.cleanPath(DIR);

      // folder path with filename
      Path path =  Paths.get(cleanFolder , cleanPath);
      System.out.println("PATH = " + path);

      // copy file to folder path
      Files.copy(inputStream , path , StandardCopyOption.REPLACE_EXISTING);

      // video meta data
      video.setContentType(contentType);
      video.setFilePath(path.toString());

      // save meta data
      return videoRepository.save(video);
    } catch (IOException e){
      e.printStackTrace();
      return null;
    }

  }

  @Override
  public Video get(String videoId) {
    return repository.findById(videoId).orElseThrow(() -> new RuntimeException("Video not found"));
  }

  @Override
  public Video getByTitle(String title) {
    return repository.findByTitle(title).orElseThrow(() -> new RuntimeException("Video not found"));
  }

  @Override
  public Iterable<Video> getAll() {
    return repository.findAll();
  }
}
