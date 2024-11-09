"use client";

import { useGlobalErrorHandler } from "@/app/context/ErrorMessageProvider";
import ProfileCard from "@/components/profile/ProfileCard/ProfileCard";
import ProfileCardSkeleton from "@/components/profile/ProfileCard/ProfileCardSkeleton";
import { Achievement } from "@/types/achievement";
import { PlayerProfile } from "@/types/player-profile";
import { Tournament } from "@/types/tournament";
import axios, { AxiosError } from "axios";
import { notFound, useRouter } from "next/navigation";
import React, { useState, useEffect } from "react";

const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}`;

const Profile = ({ params }: { params: { username: string } }) => {
  const [playerProfile, setPlayerProfile] = useState<PlayerProfile>();
  const [leaderboardData, setLeaderboardData] = useState<any>(null);
  const [achievements, setAchievements] = useState<Achievement[] | null>(null);
  const [tournaments, setTournaments] = useState<Tournament[] | null>(null);
  const { handleError } = useGlobalErrorHandler();
  const router = useRouter();

  const fetchProfile = (username: string) => {
    axios
      .get(new URL(`/profile`, API_URL).toString(), {
        params: {
          username: username,
        },
      })
      .then((response) => {
        if (response.status === 200) {
          setPlayerProfile(response.data);
        }
      })
      .catch((error: AxiosError) => {
        handleError(error);
        router.push("/notfound");
      });
  };

  const fetchLeaderboard = (username: string) => {
    axios
      .get(new URL(`/leaderboard/user`, API_URL).toString(), {
        params: {
          username: username,
        },
      })
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

  const fetchAchievements = (username: string) => {
    axios
      .get(new URL(`/profile/achievements`, API_URL).toString(), {
        params: { 
          username: username },
      })
      .then((response) => {
        if (response.status === 200) {
          setAchievements(response.data);
        }
      })
      .catch((error: AxiosError) => {
        handleError(error);
        router.push("/notfound");
      });
  };

  const fetchTournaments = (username: string) => {
    axios
      .get(new URL(`/profile/tournaments`, API_URL).toString(), {
        params: { 
          username: username },
      })
      .then((response) => {
        if (response.status === 200) {
          setTournaments(response.data);
        }
      })
      .catch((error: AxiosError) => {
        handleError(error);
        router.push("/notfound");
      });
  };

  useEffect(() => {
    // Retrieve `id` from route
    const username = params.username;

    // Redirect user to not found page if no params
    if (username == undefined) {
      notFound();
    }

    // Fetch necessary information
    fetchProfile(username);
    fetchLeaderboard(username);
    fetchAchievements(username);
    fetchTournaments(username);
  }, []);

  if (!playerProfile || !leaderboardData || !achievements || !tournaments) return <ProfileCardSkeleton />;

  return (
    <div className="bg-[#212121] text-white min-h-screen flex flex-col items-center justify-center">
      <ProfileCard
        playerProfile={playerProfile}
        ranking={leaderboardData.position}
        achievements={achievements}
        tournaments={tournaments}
      />
    </div>
  );
};

export default Profile;
