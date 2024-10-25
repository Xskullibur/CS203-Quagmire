import React from "react";
import Image from "next/image";
import { PencilLineIcon } from "lucide-react";
import { Button } from "@/components/ui/button";

interface ProfilePictureProps {
  selectedImage: string | null;
  profilePicturePath: string;
  handleChange: (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => void;
  handleClearImage: () => void;
}

const ProfilePicture: React.FC<ProfilePictureProps> = ({
  selectedImage,
  profilePicturePath,
  handleChange,
  handleClearImage,
}) => {
  return (
    <div className="mb-4 space-y-6">
      <div className="flex justify-center">
        <div
          onClick={() => document.getElementById("profilePicturePath")?.click()}
          className="relative cursor-pointer group"
        >
          <Image
            src={selectedImage || profilePicturePath}
            alt="Profile Picture"
            width={100}
            height={100}
            className="rounded-full"
          />
          <div className="absolute bottom-0 right-0 bg-white p-1 rounded-full group-hover:bg-accent transition-colors">
            <PencilLineIcon className="w-4 h-4 text-gray-700 group-hover:text-accent-foreground transition-colors" />
          </div>
        </div>
      </div>

      <input
        type="file"
        id="profilePicturePath"
        name="profilePicturePath"
        onChange={handleChange}
        className="hidden"
      />
      <div className="flex flex-col sm:flex-row sm:space-x-4 space-y-4 sm:space-y-0 justify-center items-center">
        <Button variant={"outline"} type="button" onClick={handleClearImage}>
          Clear Image
        </Button>
      </div>
    </div>
  );
};

export default ProfilePicture;
