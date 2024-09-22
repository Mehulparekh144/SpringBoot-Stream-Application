import { Toaster } from 'react-hot-toast'
import './App.css'
import VideoUploader from './component/VideoUploader'
import { Card } from 'flowbite-react';

function App() {
  return (
    <>
      <Toaster />
      <div className="flex flex-col gap-4 items-center justify-center py-2 mt-5">
        <h1 className="text-3xl font-extrabold text-zinc-800 dark:text-zinc-200">
          Stream your video !
        </h1>
        <div className="flex w-full justify-center space-x-4 items-start mt-12 p-4">
          <Card>
            <video
              width={"500"}
              src="http://localhost:8080/api/v1/videos/stream/range/af984380-d9b2-4798-9fbb-1773f71428c2"
              controls
              autoPlay
            ></video>
          </Card>
          <VideoUploader />
        </div>
      </div>
    </>
  );
}

export default App
