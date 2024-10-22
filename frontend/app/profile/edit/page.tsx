"use client";

import { useErrorHandler } from "@/app/context/ErrorMessageProvider";
import { toast } from "@/hooks/use-toast";
import { useAuth } from "@/hooks/useAuth";
import withAuth from "@/hooks/withAuth";
import axiosInstance from "@/lib/axios";
import { PlayerProfile } from "@/types/player";
import { AxiosError } from "axios";
import { useRouter } from "next/navigation";
import React, { useState, useEffect } from "react";

const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}`;

const EditProfile = () => {
  const { user } = useAuth();
  const { showErrorToast } = useErrorHandler();
  const [playerProfile, setPlayerProfile] = useState<PlayerProfile>();
  const [isLoading, setIsLoading] = useState(true);
  const router = useRouter();

  const fetchProfile = async () => {
    const response = await fetch(
      `http://localhost:8080/profile/${user?.userId}`
    );
    const data = await response.json();
    setPlayerProfile(data);
    setIsLoading(false);
  };

  useEffect(() => {
    fetchProfile();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    axiosInstance
      .put(new URL(`/profile/edit`, API_URL).toString(), {
        id: user?.userId,
        profileUpdates: playerProfile,
      })
      .then((res) => {
        if (res.status == 200) {
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
              message = "The request was invalid. Please check your input and try again.";
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
              message = "An internal server error occurred. Please try again later.";
              break;
            case 503:
              message = "The service is currently unavailable. Please try again later.";
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
    const { name, value } = e.target;
    setPlayerProfile(
      (playerProfile) =>
        ({
          ...playerProfile,
          [name]: value || null,
        }) as PlayerProfile
    );
  };
  if (isLoading) return <div>Loading...</div>;

  return (
    <div className="min-h-screen bg-[#212121] text-white flex flex-col items-center justify-center">
      <h1 className="text-3xl font-bold mb-6">Edit Profile</h1>
      <form
        onSubmit={handleSubmit}
        className="w-full max-w-2xl bg-[#171717] p-6 rounded-lg shadow-lg"
      >
        <div className="mb-4">
          <label htmlFor="firstName" className="block text-xl mb-2">
            First Name:
          </label>
          <input
            type="text"
            id="firstName"
            name="firstName"
            value={playerProfile?.firstName || ""}
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
            value={playerProfile?.lastName || ""}
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
            value={playerProfile?.bio || ""}
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
            value={playerProfile?.country || ""}
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
            value={playerProfile?.dateOfBirth || ""}
            onChange={handleChange}
            className="w-full p-2 rounded-lg bg-[#333333] text-white"
          />
        </div>
        <button
          type="submit"
          className="w-full py-2 px-4 rounded-lg bg-[#4CAF50] text-white"
        >
          Save Changes
        </button>
      </form>
    </div>
  );
};

export default withAuth(EditProfile);
