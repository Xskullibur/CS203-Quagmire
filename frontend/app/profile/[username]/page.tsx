"use client";

import { useGlobalErrorHandler } from "@/app/context/ErrorMessageProvider";
import ProfileCard from "@/components/profile/ProfileCard/ProfileCard";
import ProfileCardSkeleton from "@/components/profile/ProfileCard/ProfileCardSkeleton";
import { Achievement } from "@/types/achievement";
import { PlayerProfile } from "@/types/player-profile";
import { Tournament } from "@/types/tournament";
import axios, { AxiosError } from "axios";
import { useRouter } from "next/navigation";
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

// Custom hook for data fetching
const useProfileData = (username: string, isOwnProfile: boolean) => {
  const [data, setData] = useState({
    profile: null as PlayerProfile | null,
    leaderboard: null as any,
    achievements: null as Achievement[] | null,
    tournaments: null as Tournament[] | null,
  });
  const [loading, setLoading] = useState(true);
  const { handleError } = useGlobalErrorHandler();
  const router = useRouter();

  useEffect(() => {
    const fetchAllData = async () => {
      try {
        setLoading(true);
        
        // Fetch profile first
        const profileResponse = await axios.get(
          new URL(`/profile`, API_URL).toString(),
          { params: { username } }
        );
        
        if (!profileResponse.data && !isOwnProfile) {
          router.push("/notfound");
          return;
        }

        // Only proceed with other fetches if we have a profile or it's the user's own profile
        const [leaderboardResponse, achievementsResponse, tournamentsResponse] = await Promise.all([
          axios.get(new URL(`/leaderboard/user`, API_URL).toString(), { 
            params: { username }
          }).catch(() => ({ data: { position: "N/A" } })),
          
          axios.get(new URL(`/profile/achievements`, API_URL).toString(), { 
            params: { username }
          }).catch(() => ({ data: [] })),
          
          axios.get(new URL(`/profile/tournaments`, API_URL).toString(), { 
            params: { username }
          }).catch(() => ({ data: [] }))
        ]);

        setData({
          profile: profileResponse.data,
          leaderboard: leaderboardResponse.data,
          achievements: achievementsResponse.data,
          tournaments: tournamentsResponse.data
        });
        
      } catch (error: any) {
        if (!isOwnProfile || error.response?.status !== 404) {
          handleError(error);
          router.push("/notfound");
        }
      } finally {
        setLoading(false);
      }
    };

    if (username) {
      fetchAllData();
    }
  }, [username, isOwnProfile, handleError, router]);

  return { ...data, loading };
};

const Profile = ({ params }: { params: { username: string } }) => {
  const { isChecking, isOwnProfile } = useOwnProfileCheck(params.username);
  const { profile, leaderboard, achievements, tournaments, loading } = useProfileData(
    params.username,
    isOwnProfile
  );

  // Show loading state while checking own profile or fetching data
  if (isChecking || loading || !profile) {
    return <ProfileCardSkeleton />;
  }

  return (
    <div className="text-white min-h-screen flex flex-col items-center justify-center">
      <ProfileCard
        playerProfile={profile}
        ranking={leaderboard?.position}
        achievements={achievements || []}
        tournaments={tournaments || []}
        isOwnProfile={isOwnProfile}
      />
    </div>
  );
};

export default Profile;