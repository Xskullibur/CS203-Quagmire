"use client";

import { useGlobalErrorHandler } from "@/app/context/ErrorMessageProvider";
import ProfileCard from "@/components/profile/ProfileCard/ProfileCard";
import { PlayerProfile } from "@/types/player-profile";
import axios, { AxiosError } from "axios";
import { notFound, useRouter } from "next/navigation";
import React, { useState, useEffect } from "react";

const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}`;

const Profile = ({ params }: { params: { id: string } }) => {
  const [playerProfile, setPlayerProfile] = useState<PlayerProfile>();
  const [leaderboardData, setLeaderboardData] = useState<any>(null);
  const { handleError } = useGlobalErrorHandler();
  const router = useRouter();

  const fetchProfile = (id: string) => {
    axios
      .get(new URL(`/profile/${id}`, API_URL).toString())
      .then((response) => {
        if (response.status == 200) {
          setPlayerProfile(response.data);
        }
      })
      .catch((error: AxiosError) => {
        handleError(error);
        router.push("/notfound");
      });
  };

  const fetchLeaderboard = (id: string) => {
    axios
      .get(new URL(`leaderboard/user/${id}`, API_URL).toString())
      .then((response) => {
        if (response.status == 200) {
          setLeaderboardData(response.data);
        }
      })
      .catch((error: AxiosError) => {
        handleError(error);
        router.push("/notfound");
      });
  };

  useEffect(() => {
    // Retrieve `id` from route
    const id = params.id;

    // Redirect user to not found page if no params
    if (id == undefined) {
      notFound();
    }

    // Fetch necessary information
    fetchProfile(id);
    fetchLeaderboard(id);
  }, []);

  if (!playerProfile || !leaderboardData)
    return <div>Unable to locate user</div>;

  return (
    <div className="bg-[#212121] text-white min-h-screen flex flex-col items-center justify-center">
      <ProfileCard
        playerProfile={playerProfile}
        ranking={leaderboardData.position}
      />
      {/* My Tournaments */}
    </div>
  );
};

export default Profile;
