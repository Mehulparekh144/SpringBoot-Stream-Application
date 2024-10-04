import React, { useEffect } from "react";
import videojs from "video.js";
import Hls from "hls.js";
import "video.js/dist/video-js.css";
import Player from "video.js/dist/types/player";
import toast from "react-hot-toast";

const VideoPlayer = ({ src }: { src: string }) => {
  const videoRef = React.useRef<HTMLVideoElement>(null);
  const playerRef = React.useRef<Player | null>(null);

  // Use the useEffect hook to perform side effects in the component
  useEffect(() => {
    // Check if the videoRef is currently pointing to a DOM element
    if (videoRef.current) {
      // Initialize the video.js player with the videoRef and configuration options
      playerRef.current = videojs(videoRef.current, {
        controls: true, // Show video controls
        muted : true,
        preload: "auto", // Preload video as soon as possible without waiting
        sources: [
          {
            src, // Video source URL
            type: "application/x-mpegURL", // MIME type of the video source
          },
        ],
      });

      // Check if HLS (HTTP Live Streaming) is supported in the current environment
      if (Hls.isSupported()) {
        const hls = new Hls(); // Create a new Hls instance
        hls.loadSource(src); // Load the video source into the HLS player
        hls.attachMedia(videoRef.current); // Attach the media element to the HLS player
        // Listen for the MANIFEST_PARSED event, which indicates the manifest has been loaded and parsed
        hls.on(Hls.Events.MANIFEST_PARSED, function () {
          videoRef.current?.play(); // Play the video once the manifest is parsed
        });
      } else if (
        videoRef.current.canPlayType("application/vnd.apple.mpegurl")
      ) {
        // Fallback for browsers that can play HLS without needing Hls.js
        videoRef.current.src = src; // Set the video source directly on the video element
        videoRef.current.addEventListener("canplay", function () {
          videoRef.current?.play(); // Play the video once it can play
        });
      } else {
        // Show an error message if HLS is not supported in the browser
        toast.error("Your browser does not support HLS video playback");
      }
    }
  }, [src]); // Re-run the effect if the src prop changes

  return (
    <div data-vjs-player>
      <video ref={videoRef} className="video-js vjs-default-skin vjs-big-play-centered">
      </video>
    </div>
  )
};

export default VideoPlayer;
