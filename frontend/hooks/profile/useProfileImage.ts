import { useState } from "react";
import { toast } from "@/hooks/use-toast";
import { validateImageFile, fetchDefaultImage, cleanupObjectURL } from "@/utils/imageUtils";

const PROFILE_IMAGE_API = 'https://api.dicebear.com/9.x/initials/png?fontFamily=Georgia&backgroundType=gradientLinear&seed=';

interface ProfileImageState {
  currentImageUrl: string;
  isDefaultImage: boolean;
  profileImage: File | null;
}

export const useProfileImage = (initialUsername?: string) => {
  const [imageState, setImageState] = useState<ProfileImageState>({
    currentImageUrl: initialUsername ? `${PROFILE_IMAGE_API}${initialUsername}` : "",
    isDefaultImage: true,
    profileImage: null,
  });

  const updateImageState = (updates: Partial<ProfileImageState>) => {
    setImageState(prev => ({ ...prev, ...updates }));
  };

  const handleImageSelect = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const validation = validateImageFile(file);
    if (!validation.isValid) {
      toast({
        variant: "destructive",
        title: "Invalid File",
        description: validation.error,
      });
      return;
    }

    cleanupObjectURL(imageState.currentImageUrl);
    const imageUrl = URL.createObjectURL(file);

    updateImageState({
      currentImageUrl: imageUrl,
      isDefaultImage: false,
      profileImage: file,
    });

    return imageUrl;
  };

  const setDefaultImage = async (username: string) => {
    if (!username) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Username not found",
      });
      return null;
    }

    try {
      const defaultImageUrl = `${PROFILE_IMAGE_API}${username}`;
      const defaultImageFile = await fetchDefaultImage(
        defaultImageUrl,
        `${username}-default.png`
      );

      cleanupObjectURL(imageState.currentImageUrl);

      updateImageState({
        currentImageUrl: defaultImageUrl,
        isDefaultImage: true,
        profileImage: defaultImageFile,
      });

      return defaultImageUrl;
    } catch (error) {
      const defaultImageUrl = `${PROFILE_IMAGE_API}${username}`;
      updateImageState({
        currentImageUrl: defaultImageUrl,
        isDefaultImage: true,
        profileImage: null,
      });
      
      toast({
        variant: "destructive",
        title: "Warning",
        description: "Using fallback default image",
      });
      return defaultImageUrl;
    }
  };

  return {
    imageState,
    handleImageSelect,
    setDefaultImage,
    updateImageState,
  };
};