"use client";

import React, { useEffect, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
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

interface ProfileState {
  isLoading: boolean;
  selectedImage: string | null;
  isDefaultImage: boolean;
  playerProfileRequest: PlayerProfileRequest;
}

const createInitialState = (
  userId: string | undefined,
  username: string | undefined
): ProfileState => ({
  isLoading: true,
  selectedImage: null,
  isDefaultImage: true,
  playerProfileRequest: {
    id: userId ?? "",
    profileUpdates: {
      profilePicturePath: `${PROFILE_IMAGE_API}${username}`,
    } as PlayerProfile,
    profileImage: null,
  },
});

const EditProfile = () => {
  const router = useRouter();
  const { user } = useAuth();
  const { handleError } = useGlobalErrorHandler();
  const searchParams = useSearchParams();
  const isNewProfile = searchParams.get("new") === "true";

  const checkProfileAndRedirect = async () => {
    try {
      const response = await axios.get(
        new URL(`/profile/${user?.userId}`, API_URL).toString()
      );

      // If we're trying to create a new profile but one exists, redirect to edit
      if (isNewProfile && response.status === 200) {
        toast({
          variant: "default",
          title: "Profile Exists",
          description: "You already have a profile.",
        });
        router.replace("/profile/edit");
        return true;
      }

      // If we're trying to edit a non-existent profile, redirect to create
      if (!isNewProfile && response.status === 404) {
        toast({
          variant: "default",
          title: "No Profile Found",
          description: "Please create your profile.",
        });
        router.replace("/profile/edit?new=true");
        return false;
      }

      return response.status === 200;
    } catch (error) {
      if (axios.isAxiosError(error)) {
        if (error.response?.status === 404 && !isNewProfile) {
          // Profile doesn't exist and we're not in create mode
          toast({
            variant: "default",
            title: "No Profile Found",
            description: "Please create your profile.",
          });
          router.replace("/profile/edit?new=true");
          return false;
        }

        handleError(error);
      }
      return false;
    }
  };

  const [state, setState] = useState<ProfileState>(
    createInitialState(user?.userId, user?.username)
  );

  // Profile initialization and cleanup
  useEffect(() => {
    const initProfile = async () => {
      const profileExists = await checkProfileAndRedirect();

      // Only continue with initialization if we're in the correct mode
      if (
        (isNewProfile && !profileExists) ||
        (!isNewProfile && profileExists)
      ) {
        if (isNewProfile) {
          await initializeNewProfile();
        } else {
          await fetchExistingProfile();
        }
      }
    };

    initProfile();
    return () => cleanupObjectURL(state.selectedImage);
  }, [user?.userId, isNewProfile]);

  // Profile initialization functions
  const initializeNewProfile = async () => {
    try {
      const defaultImageUrl = `${PROFILE_IMAGE_API}${user?.username}`;
      const defaultImageFile = await fetchDefaultImage(
        defaultImageUrl,
        `${user?.username}-default.png`
      );

      setState((prev) => ({
        ...prev,
        isLoading: false,
        selectedImage: defaultImageUrl,
        isDefaultImage: true,
        playerProfileRequest: {
          ...prev.playerProfileRequest,
          profileImage: defaultImageFile,
          profileUpdates: {
            ...prev.playerProfileRequest.profileUpdates,
            username: user?.username ?? "",
            profilePicturePath: defaultImageUrl,
          },
        },
      }));
    } catch (error) {
      if (axios.isAxiosError(error)) {
        handleError(error);
      }
      setState((prev) => ({ ...prev, isLoading: false }));
    }
  };

  const fetchExistingProfile = async () => {
    try {
      const response = await axios.get(
        new URL(`/profile/${user?.userId}`, API_URL).toString()
      );
      const profileData: PlayerProfile = response.data;

      const isDefault =
        !profileData.profilePicturePath ||
        profileData.profilePicturePath.includes(PROFILE_IMAGE_API!);

      const profilePicturePath = !profileData.profilePicturePath
        ? `${PROFILE_IMAGE_API}${profileData.username}`
        : profileData.profilePicturePath;

      setState((prev) => ({
        ...prev,
        isLoading: false,
        isDefaultImage: isDefault,
        playerProfileRequest: {
          ...prev.playerProfileRequest,
          profileUpdates: {
            ...profileData,
            profilePicturePath: addCacheBustingParameter(profilePicturePath),
          },
        },
      }));
    } catch (error) {
      if (axios.isAxiosError(error)) {
        handleError(error);
      }

      setState((prev) => ({ ...prev, isLoading: false }));
    }
  };

  // Form handlers
  const handleFormSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const formData = createFormData();

    try {
      await updateProfile(formData);
    } catch (error) {
      if (axios.isAxiosError(error)) {
        handleError(error);
      }
    }
  };

  const createFormData = () => {
    const formData = new FormData();
    formData.append("id", state.playerProfileRequest.id);
    formData.append(
      "profileUpdates",
      new Blob([JSON.stringify(state.playerProfileRequest.profileUpdates)], {
        type: "application/json",
      })
    );

    if (state.playerProfileRequest.profileImage) {
      formData.append("profileImage", state.playerProfileRequest.profileImage);
    }

    return formData;
  };

  const updateProfile = async (formData: FormData) => {
    axiosInstance({
      method: isNewProfile ? "post" : "put",
      url: `${API_URL}/profile`,
      data: formData,
      headers: { "Content-Type": "multipart/form-data" },
    }).then(() => {
      toast({
        variant: "success",
        title: "Success",
        description: isNewProfile
          ? "Profile created successfully."
          : "Profile updated successfully.",
      });

      router.push(`/profile/${state.playerProfileRequest.id}`);
    });
  };

  // Input handlers
  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setState((prev) => ({
      ...prev,
      playerProfileRequest: {
        ...prev.playerProfileRequest,
        profileUpdates: {
          ...prev.playerProfileRequest.profileUpdates,
          [name]: value || null,
        },
      },
    }));
  };

  // Image handlers
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

    cleanupObjectURL(state.selectedImage);
    const imageUrl = URL.createObjectURL(file);

    setState((prev) => ({
      ...prev,
      selectedImage: imageUrl,
      isDefaultImage: false,
      playerProfileRequest: {
        ...prev.playerProfileRequest,
        profileImage: file,
        profileUpdates: {
          ...prev.playerProfileRequest.profileUpdates,
          profilePicturePath: imageUrl,
        },
      },
    }));
  };

  const handleResetToDefault = async () => {
    try {
      const defaultImageUrl = `${PROFILE_IMAGE_API}${state.playerProfileRequest.profileUpdates.username}`;
      const defaultImageFile = await fetchDefaultImage(
        defaultImageUrl,
        `${state.playerProfileRequest.profileUpdates.username}-default.png`
      );

      cleanupObjectURL(state.selectedImage);

      setState((prev) => ({
        ...prev,
        selectedImage: null,
        isDefaultImage: true,
        playerProfileRequest: {
          ...prev.playerProfileRequest,
          profileImage: defaultImageFile,
          profileUpdates: {
            ...prev.playerProfileRequest.profileUpdates,
            profilePicturePath: defaultImageUrl,
          },
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

  if (state.isLoading) return <div>Loading...</div>;

  return (
    <div className="flex flex-col items-center min-h-screen mt-24">
      <Card className="w-full flex flex-col max-w-2xl">
        <form onSubmit={handleFormSubmit}>
          <CardHeader>
            <CardTitle className="text-center">
              {isNewProfile ? "Create Profile" : "Edit Profile"}
            </CardTitle>
          </CardHeader>

          <CardContent>
            <ProfilePicture
              currentImageUrl={
                state.selectedImage ??
                addCacheBustingParameter(
                  state.playerProfileRequest.profileUpdates.profilePicturePath
                )
              }
              isDefaultImage={state.isDefaultImage}
              onImageSelect={handleImageSelect}
              onReset={handleResetToDefault}
            />

            <FormFields
              profileData={state.playerProfileRequest.profileUpdates}
              onChange={handleInputChange}
            />
          </CardContent>

          <CardFooter>
            <Button type="submit">
              {isNewProfile ? "Create Profile" : "Save Changes"}
            </Button>
          </CardFooter>
        </form>
      </Card>
    </div>
  );
};

export default withAuth(EditProfile);
