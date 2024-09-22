import axios, { AxiosProgressEvent } from "axios";
import {
  Button,
  Card,
  FileInput,
  Label,
  Progress,
  Spinner,
  Textarea,
  TextInput,
} from "flowbite-react";
import { useState } from "react";
import toast from "react-hot-toast";

function VideoUploader() {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [progress, setProgress] = useState<number>(0);
  const [uploading, setUploading] = useState<boolean>(false);
  const [meta, setMeta] = useState({
    title: "",
    description: "",
  });

  function handleFileChange(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files ? e.target.files[0] : null;
    setSelectedFile(file as any);
  }

  function handleFormChange(
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) {
    setMeta({
      ...meta,
      [e.target.name]: e.target.value,
    });
  }

  function handleForm(e: React.FormEvent<EventTarget>) {
    e.preventDefault();
    if (!selectedFile) {
      alert("Please select a file");
    }
    submitFile(selectedFile, meta);
    (e.target as HTMLFormElement).reset();
  }

  async function submitFile(file: any, videoMetadata: any) {
    setUploading(true);
    try {
      const formData = new FormData();
      formData.append("file", file);
      formData.append("title", videoMetadata.title);
      formData.append("description", videoMetadata.description);
      await axios.post(
        "http://localhost:8080/api/v1/videos",
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
          onUploadProgress: (progressEvent: AxiosProgressEvent) => {
            if(progressEvent.total){
              setProgress(
                Math.round((progressEvent.loaded / progressEvent.total) * 100)
              );
            }
          },
        }
      );

      setUploading(false);
      toast.success("Video uploaded successfully");
      setTimeout(() => {
        setProgress(0);
      }, 3000);
      setSelectedFile(null);
    } catch (e) {
      console.log(e);
      toast.error("Failed to upload video");
      setUploading(false);
    }
  }

  return (
    <Card>
      <form
        onSubmit={handleForm}
        className="flex flex-col gap-3 w-full items-center justify-center"
      >
        <div className="w-full">
          <div className="mb-2 block">
            <Label htmlFor="title1" value="Video Title" />
          </div>
          <TextInput
            id="title1"
            name="title"
            type="text"
            onChange={(e) => handleFormChange(e)}
            placeholder="Enter video title"
            required
          />
        </div>
        <div className="w-full">
          <div className="mb-2 block">
            <Label htmlFor="description1" value="Video Description" />
          </div>
          <Textarea
            id="description1"
            name="description"
            onChange={(e) => handleFormChange(e)}
            placeholder="Enter video description"
            required
          />
        </div>
        <Label
          htmlFor="dropzone-file"
          className="flex h-64 w-full cursor-pointer flex-col items-center justify-center rounded-lg border-2 border-dashed border-gray-300 bg-gray-50 hover:bg-gray-100 dark:border-gray-600 dark:bg-gray-700 dark:hover:border-gray-500 dark:hover:bg-gray-600 px-4"
        >
          <div className="flex flex-col items-center justify-center pb-6 pt-5">
            {
              selectedFile ? <div>
                <h1>{selectedFile.name}</h1>
              </div> :
              <>
              <svg
              className="mb-4 h-8 w-8 text-gray-500 dark:text-gray-400"
              aria-hidden="true"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 20 16"
            >
              <path
                stroke="currentColor"
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="2"
                d="M13 13h3a3 3 0 0 0 0-6h-.025A5.56 5.56 0 0 0 16 6.5 5.5 5.5 0 0 0 5.207 5.021C5.137 5.017 5.071 5 5 5a4 4 0 0 0 0 8h2.167M10 15V6m0 0L8 8m2-2 2 2"
              />
            </svg>
            <p className="mb-2 text-sm text-gray-500 dark:text-gray-400">
              <span className="font-semibold">Click to upload</span>
            </p>
            <p className="text-xs text-gray-500 dark:text-gray-400">
              All Video files are supported ( MP4 , MKV , AVI , MOV , FLV , WMV
              , WEBM , 3GP )
            </p>
              </>
            
            }
          </div>
          <FileInput
            onChange={handleFileChange}
            id="dropzone-file"
            className="hidden"
          />
        </Label>
        {progress > 0 && <Progress className="w-96" progress={progress} />}
        <Button type="submit" disabled={uploading}>
          {uploading && <Spinner size={"sm"} />}
          Upload
        </Button>
      </form>
    </Card>
  );
}

export default VideoUploader;
