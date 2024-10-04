import { Toaster } from 'react-hot-toast'
import './App.css'
import VideoUploader from './component/VideoUploader'
import { Card } from 'flowbite-react';
import VideoPlayer from './component/VideoPlayer';

function App() {
  return (
    <>
      <Toaster />
      <div className="flex flex-col gap-4 items-center justify-center py-2 mt-5">
        <h1 className="text-3xl font-extrabold text-zinc-800 dark:text-zinc-200">
          Stream your video !
        </h1>
        <div className="flex flex-col md:flex-row  w-full justify-center gap-4 md:items-start mt-12 p-4">
          <Card>
            <VideoPlayer src="http://localhost:8080/api/v1/videos/4e485889-ad47-474d-939e-39c03dc00c29/master.m3u8" />
            {/* <video
            style={{
              width : "100%"
            }}
              src="http://localhost:8080/api/v1/videos/4e485889-ad47-474d-939e-39c03dc00c29/master.m3u8"
              controls
              autoPlay
            ></video> */}
          </Card>
          <VideoUploader />
        </div>
      </div>
    </>
  );
}

export default App
