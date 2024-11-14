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
import { useAuth } from "@/hooks/useAuth";

const API_URL = `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}`;

// Custom hook to check if viewing own profile
const useOwnProfileCheck = (username: string) => {
  const { user, isLoading: authLoading } = useAuth();
  const [isChecking, setIsChecking] = useState(true);
  const router = useRouter();

  useEffect(() => {
    const checkOwnProfile = async () => {
      if (authLoading) return;

      if (user && user.username === username) {
        try {
          await axios.get(`${API_URL}/profile/${user.userId}`);
        } catch (error: any) {
          if (error.response?.status === 404) {
            router.push(`/profile/edit?new=true`);
            return;
          }
        }
      }
      setIsChecking(false);
    };

    checkOwnProfile();
  }, [user, username, authLoading, router]);

  return { isChecking, isOwnProfile: user?.username === username };
};

const Profile = ({ params }: { params: { username: string } }) => {
  const [playerProfile, setPlayerProfile] = useState<PlayerProfile>();
  const [leaderboardData, setLeaderboardData] = useState<any>(null);
  const [achievements, setAchievements] = useState<Achievement[] | null>(null);
  const [tournaments, setTournaments] = useState<Tournament[] | null>(null);
  const { handleError } = useGlobalErrorHandler();
  const router = useRouter();
  const { isChecking: isCheckingOwnProfile, isOwnProfile } = useOwnProfileCheck(params.username);

  const fetchProfile = React.useCallback((username: string) => {
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
        // Only redirect to notfound if it's not the user's own profile
        if (!isOwnProfile || error.response?.status !== 404) {
          handleError(error);
          router.push("/notfound");
        }
      });
  }, [handleError, router, isOwnProfile]);

  const fetchLeaderboard = React.useCallback((username: string) => {
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
        // Only handle errors for non-404 cases when viewing own profile
        if (!isOwnProfile || error.response?.status !== 404) {
          handleError(error);
          router.push("/notfound");
        }
      });
  }, [handleError, router, isOwnProfile]);

  const fetchAchievements = React.useCallback((username: string) => {
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
        if (!isOwnProfile || error.response?.status !== 404) {
          handleError(error);
          router.push("/notfound");
        }
      });
  }, [handleError, router, isOwnProfile]);

  const fetchTournaments = React.useCallback((username: string) => {
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
        if (!isOwnProfile || error.response?.status !== 404) {
          handleError(error);
          router.push("/notfound");
        }
      });
  }, [handleError, router, isOwnProfile]);

  useEffect(() => {
    // Only fetch data if we're done checking own profile status
    if (!isCheckingOwnProfile) {
      fetchProfile(params.username);
      fetchLeaderboard(params.username);
      fetchAchievements(params.username);
      fetchTournaments(params.username);
    }
  }, [
    fetchAchievements,
    fetchLeaderboard,
    fetchProfile,
    fetchTournaments,
    params.username,
    isCheckingOwnProfile
  ]);

  // Show loading state while checking profile or fetching data
  if (isCheckingOwnProfile || !playerProfile || !leaderboardData || !achievements || !tournaments) {
    return <ProfileCardSkeleton />;
  }

  return (
    <div className="text-white min-h-screen flex flex-col items-center justify-center">
      <ProfileCard
        playerProfile={playerProfile}
        ranking={leaderboardData.position}
        rankPercentage = {leaderboardData.rankPercentage}
        achievements={achievements}
        tournaments={tournaments}
        isOwnProfile={isOwnProfile}
      />
    </div>
  );
};

export default Profile;