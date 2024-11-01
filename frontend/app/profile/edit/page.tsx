"use client";

import React, { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useGlobalErrorHandler } from "@/app/context/ErrorMessageProvider";
import { toast } from "@/hooks/use-toast";
import { useAuth } from "@/hooks/useAuth";
import withAuth from "@/hooks/withAuth";
import axiosInstance from "@/lib/axios";
import { PlayerProfile } from "@/types/player-profile";
import { PlayerProfileRequest } from "@/types/player-profile-request";
import {
  validateImageFile,
  fetchDefaultImage,
  cleanupObjectURL,
  addCacheBustingParameter,
} from "@/utils/imageUtils";
import ProfilePicture from "@/components/profile/ProfilePicture";
import FormFields from "@/components/profile/FormFields";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import axios from "axios";

const API_URL = process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL;
const PROFILE_IMAGE_API = process.env.NEXT_PUBLIC_PROFILEPICTURE_API_URL;

const EditProfile = () => {
  const router = useRouter();
  const { user } = useAuth();
  const { handleError } = useGlobalErrorHandler();

  const [isLoading, setIsLoading] = useState(true);
  const [selectedImage, setSelectedImage] = useState<string | null>(null);
  const [isDefaultImage, setIsDefaultImage] = useState(true);
  const [playerProfileRequest, setPlayerProfileRequest] =
    useState<PlayerProfileRequest>({
      id: user?.userId ?? "",
      profileUpdates: {} as PlayerProfile,
      profileImage: null,
    });

  // Cleanup object URLs on unmount
  useEffect(() => {
    return () => {
      cleanupObjectURL(selectedImage);
    };
  }, [selectedImage]);

  const fetchProfile = async () => {
    try {
      const response = await fetch(`${API_URL}/profile/${user?.userId}`);
      const data = await response.json();

      const isDefault =
        !data.profilePicturePath ||
        data.profilePicturePath.includes(PROFILE_IMAGE_API);

      setIsDefaultImage(isDefault);

      // Add cache busting to the profile picture URL
      const profilePicturePath = !data.profilePicturePath
        ? `${PROFILE_IMAGE_API}${data.username}`
        : data.profilePicturePath;

      data.profilePicturePath = addCacheBustingParameter(profilePicturePath);

      setPlayerProfileRequest((prev) => ({
        ...prev,
        profileUpdates: data,
      }));
    } catch (error) {
      if (axios.isAxiosError(error)) {
        handleError(error);
      }
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchProfile();
  }, [user?.userId]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const formData = new FormData();
    formData.append("id", playerProfileRequest.id);
    formData.append(
      "profileUpdates",
      new Blob([JSON.stringify(playerProfileRequest.profileUpdates)], {
        type: "application/json",
      })
    );

    if (playerProfileRequest.profileImage) {
      formData.append("profileImage", playerProfileRequest.profileImage);
    }

    try {
      const response = await axiosInstance.put(`${API_URL}/profile`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      if (response.status === 200) {
        toast({
          variant: "success",
          title: "Success",
          description: "Profile updated successfully.",
        });
        router.push(`/profile/${playerProfileRequest.id}`);
      }
    } catch (error) {
      if (axios.isAxiosError(error)) {
        handleError(error);
      }
    }
  };

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setPlayerProfileRequest((prev) => ({
      ...prev,
      profileUpdates: {
        ...prev.profileUpdates,
        [name]: value || null,
      },
    }));
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

    cleanupObjectURL(selectedImage);

    const imageUrl = URL.createObjectURL(file);
    setSelectedImage(imageUrl);
    setIsDefaultImage(false);
    setPlayerProfileRequest((prev) => ({
      ...prev,
      profileImage: file,
      profileUpdates: {
        ...prev.profileUpdates,
        profilePicturePath: imageUrl,
      },
    }));
  };

  const handleResetToDefault = async () => {
    try {
      const defaultImageUrl = `${PROFILE_IMAGE_API}${playerProfileRequest.profileUpdates.username}`;
      const defaultImageFile = await fetchDefaultImage(
        defaultImageUrl,
        `${playerProfileRequest.profileUpdates.username}-default.png`
      );

      cleanupObjectURL(selectedImage);

      setSelectedImage(null);
      setIsDefaultImage(true);
      setPlayerProfileRequest((prev) => ({
        ...prev,
        profileImage: defaultImageFile,
        profileUpdates: {
          ...prev.profileUpdates,
          profilePicturePath: defaultImageUrl,
        },
      }));
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: "Failed to reset to default image",
      });
    }
  };

  if (isLoading) return <div>Loading...</div>;

  return (
    <div className="flex flex-col items-center min-h-screen mt-24">
      <Card className="w-full flex flex-col max-w-2xl">
        <form onSubmit={handleSubmit}>
          <CardHeader>
            <CardTitle className="text-center">Edit Profile</CardTitle>
          </CardHeader>

          <CardContent>
            <ProfilePicture
              currentImageUrl={
                selectedImage ??
                addCacheBustingParameter(
                  playerProfileRequest.profileUpdates.profilePicturePath
                )
              }
              isDefaultImage={isDefaultImage}
              onImageSelect={handleImageSelect}
              onReset={handleResetToDefault}
            />

            <FormFields
              profileData={playerProfileRequest.profileUpdates}
              onChange={handleInputChange}
            />
          </CardContent>

          <CardFooter>
            <Button type="submit">Save Changes</Button>
          </CardFooter>
        </form>
      </Card>
    </div>
  );
};

export default withAuth(EditProfile);
