"use client";

import { useErrorHandler } from "@/app/context/ErrorMessageProvider";
import { toast } from "@/hooks/use-toast";
import { useAuth } from "@/hooks/useAuth";
import withAuth from "@/hooks/withAuth";
import axiosInstance from "@/lib/axios";
import { PlayerProfile } from "@/types/player-profile";
import { PlayerProfileRequest } from "@/types/player-profile-request";
import { AxiosError } from "axios";
import { useRouter } from "next/navigation";
import React, { useEffect, useState } from "react";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import ProfilePicture from "@/components/profile/EditProfilePicture";
import { validateImageFile } from "@/utils/fileValidation";

const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}`;
const PROFILE_IMAGE_API = `${process.env.NEXT_PUBLIC_PROFILEPICTURE_API_URL}`;

const EditProfile = () => {
  const { user } = useAuth();
  const { showErrorToast } = useErrorHandler();
  const [playerProfileRequest, setPlayerProfileRequest] =
    useState<PlayerProfileRequest>({
      id: user?.userId || "",
      profileUpdates: {} as PlayerProfile,
      profileImage: new File([], ""),
    });
  const [isLoading, setIsLoading] = useState(true);
  const [selectedImage, setSelectedImage] = useState<string | null>(null);
  const router = useRouter();

  const fetchProfile = async () => {
    const response = await fetch(`${API_URL}/profile/${user?.userId}`);
    const data = await response.json();

    if (!data.profilePicturePath) {
      data.profilePicturePath = PROFILE_IMAGE_API + data.username;
    }

    setPlayerProfileRequest((prev) => ({
      ...prev,
      profileUpdates: data,
    }));

    setIsLoading(false);
  };

  useEffect(() => {
    fetchProfile();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Create FormData object
    const formData = new FormData();

    // Add the id
    formData.append("id", playerProfileRequest.id);

    // Add the profile updates as a JSON string
    formData.append(
      "profileUpdates",
      new Blob([JSON.stringify(playerProfileRequest.profileUpdates)], {
        type: "application/json",
      })
    );

    // Add the profile image if it exists and is not empty
    if (playerProfileRequest.profileImage.size > 0) {
      formData.append("profileImage", playerProfileRequest.profileImage);
    }

    axiosInstance
      .put(new URL(`/profile`, API_URL).toString(), formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      })
      .then((response) => {
        if (response.status === 200) {
          toast({
            variant: "success",
            title: "Success",
            description: "Profile updated.",
          });
          router.push("/profile/" + user?.userId);
        }
      })
      .catch((error: AxiosError) => {
        let title = "Error";
        let message = error.message;

        if (error.response) {
          switch (error.response.status) {
            case 400:
              message =
                "The request was invalid. Please check your input and try again.";
              break;
            case 401:
              message = "You are not authorized to edit this profile.";
              break;
            case 403:
              message = "You do not have permission to perform this action.";
              break;
            case 404:
              message = "The profile you are trying to edit does not exist.";
              break;
            case 500:
              message =
                "An internal server error occurred. Please try again later.";
              break;
            case 503:
              message =
                "The service is currently unavailable. Please try again later.";
              break;
            default:
              message = "An unexpected error occurred. Please try again.";
          }
        }

        showErrorToast(title, message);
      });
  };

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value, files } = e.target as HTMLInputElement;
    if (name === "profilePicturePath" && files && files[0]) {
      const file = files[0];

      // Validate the image file
      const validation = validateImageFile(file);
      if (!validation.isValid) {
        showErrorToast("Invalid File", validation.error || "Invalid file");
        return;
      }

      const imageUrl = URL.createObjectURL(file);
      setSelectedImage(imageUrl);
      setPlayerProfileRequest((prev) => ({
        ...prev,
        profileUpdates: {
          ...prev.profileUpdates,
          profilePicturePath: imageUrl,
        },
        profileImage: file,
      }));
    } else {
      setPlayerProfileRequest((prev) => ({
        ...prev,
        profileUpdates: {
          ...prev.profileUpdates,
          [name]: value || null,
        },
      }));
    }
  };

  const handleClearImage = () => {
    setSelectedImage(null);
    setPlayerProfileRequest((prev) => ({
      ...prev,
      profileUpdates: {
        ...prev.profileUpdates,
        profilePicturePath:
          PROFILE_IMAGE_API + playerProfileRequest.profileUpdates.username,
      },
      profileImage: new File([], ""),
    }));
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
            {/* Profile Picture */}
            <ProfilePicture
              selectedImage={selectedImage}
              profilePicturePath={
                playerProfileRequest.profileUpdates.profilePicturePath
              }
              handleChange={handleChange}
              handleClearImage={handleClearImage}
            />

            {/* First Name */}
            <div className="mb-4">
              <label htmlFor="firstName" className="block text-xl mb-2">
                First Name:
              </label>
              <input
                type="text"
                id="firstName"
                name="firstName"
                value={playerProfileRequest.profileUpdates.firstName || ""}
                onChange={handleChange}
                className="w-full p-2 rounded-lg bg-[#333333] text-white"
              />
            </div>
            <div className="mb-4">
              <label htmlFor="lastName" className="block text-xl mb-2">
                Last Name:
              </label>
              <input
                type="text"
                id="lastName"
                name="lastName"
                value={playerProfileRequest.profileUpdates.lastName || ""}
                onChange={handleChange}
                className="w-full p-2 rounded-lg bg-[#333333] text-white"
              />
            </div>
            <div className="mb-4">
              <label htmlFor="bio" className="block text-xl mb-2">
                Bio:
              </label>
              <textarea
                id="bio"
                name="bio"
                value={playerProfileRequest.profileUpdates.bio || ""}
                onChange={handleChange}
                className="w-full p-2 rounded-lg bg-[#333333] text-white"
              />
            </div>
            <div className="mb-4">
              <label htmlFor="country" className="block text-xl mb-2">
                Country:
              </label>
              <input
                type="text"
                id="country"
                name="country"
                value={playerProfileRequest.profileUpdates.country || ""}
                onChange={handleChange}
                className="w-full p-2 rounded-lg bg-[#333333] text-white"
              />
            </div>
            <div className="mb-4">
              <label htmlFor="dateOfBirth" className="block text-xl mb-2">
                Date of Birth:
              </label>
              <input
                type="date"
                id="dateOfBirth"
                name="dateOfBirth"
                value={playerProfileRequest.profileUpdates.dateOfBirth || ""}
                onChange={handleChange}
                className="w-full p-2 rounded-lg bg-[#333333] text-white"
              />
            </div>
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
