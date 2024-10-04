package com.video.streaming.services;

import com.video.streaming.entities.Video;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface VideoService {

  // save video
  Video save(Video video , MultipartFile file) throws IOException;

  // get video by id
  Video get(String videoId);

  // get video by title
  Video getByTitle(String title);

  // get all videos
  Iterable<Video> getAll();

  // video processing
  String processVideo(String videoId);

}
