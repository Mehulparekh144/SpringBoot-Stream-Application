package com.video.streaming;

import com.video.streaming.services.VideoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private VideoService videoService;

	@Test
	void contextLoads() {
		String expected = "Video processed successfully";
		String actual =	videoService.processVideo("2c0c7ec2-aa0b-40d4-ac06-835b46b03339");
		assert(expected.equals(actual));
	}

}
