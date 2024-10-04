## Transcoding 
Transcoding is the conversion of compressed digital media between different formats12. It involves:
Decoding the original media file into an uncompressed intermediate format
Re-encoding it into a new target format12
This allows digital content to be adapted for different devices, bandwidths, and purposes.
How Transcoding Works
## Importance of Transcoding
Transcoding plays a critical role in video streaming and content delivery by:
Enabling compatibility across diverse devices and platforms
Optimizing for different bandwidths and network conditions
Supporting adaptive bitrate streaming
Reducing file sizes while maintaining quality
Facilitating content distribution and accessibility

### For spring boot
We use ffmpeg to transcode the video files. 

Make use of HLS streaming to stream the video files.
HLS stands for HTTP Live Streaming. 

- Segment the video file into small chunks of 2-10 seconds
- Create a playlist file that contains the list of video chunks `.m3u8 file`
- Each quality level has its own playlist file, which contains the list of video chunks for that quality level

This will help us to implement adaptive bitrate streaming.