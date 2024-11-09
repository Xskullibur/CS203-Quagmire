import React from "react";
import Image from "next/image";
import { PencilLineIcon } from "lucide-react";
import { Button } from "@/components/ui/button";

interface ProfilePictureProps {
  currentImageUrl: string;
  isDefaultImage: boolean;
  onImageSelect: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onReset: () => void;
}

const ProfilePicture: React.FC<ProfilePictureProps> = ({
  currentImageUrl,
  isDefaultImage,
  onImageSelect,
  onReset,
}) => (
  <div className="mb-4 space-y-4">
    <div className="flex justify-center">
      <Button
        onClick={() => document.getElementById("profilePicturePath")?.click()}
        variant="image"
        className="relative cursor-pointer group p-0"
        style={{ width: 'auto', height: 'auto' }}
        type="button"
      >
        <Image
          src={currentImageUrl}
          alt="Profile Picture"
          width={100}
          height={100}
          className="rounded-full"
          priority
          // Add these props to prevent caching
          unoptimized
          loading="eager"
        />
        <div className="absolute bottom-0 right-0 bg-white p-1 rounded-full group-hover:bg-accent transition-colors">
          <PencilLineIcon className="w-4 h-4 text-gray-700 group-hover:text-accent-foreground transition-colors" />
        </div>
      </Button>
    </div>

    <input
      type="file"
      id="profilePicturePath"
      name="profilePicturePath"
      onChange={onImageSelect}
      className="hidden"
      accept="image/*"
    />
    
    {!isDefaultImage && (
      <div className="flex justify-center">
        <Button variant="outline" onClick={onReset} type="button">
          Reset to Default
        </Button>
      </div>
    )}
  </div>
);

export default ProfilePicture;