"use client";
import React, { useEffect, useState } from "react";
import axiosInstance from "@/lib/axios";
import { useGlobalErrorHandler } from "@/app/context/ErrorMessageProvider";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/hooks/useAuth";
import axios from "axios";
import { User } from "@/types/user";
import { PlayerProfile } from "@/types/player-profile";
import withAuth from "@/hooks/withAuth";

interface Tournament {
  name: string;
  startDate: string;
  endDate: string;
  description: string;
  deadline: string;
  location: string;
}

const TournamentDetails: React.FC<{ params: { id: string } }> = ({
  params,
}) => {
  const { id } = params;
  const { user, isAuthenticated } = useAuth(); // Accessing user context via useAuth hook
  const userId = user?.userId; // Retrieve userId from user object
  const [tournament, setTournament] = useState<Tournament | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [registrationClosed, setRegistrationClosed] = useState<boolean>(false);
  const { handleError } = useGlobalErrorHandler();
  const [registered, setRegistered] = useState<boolean>(false);

  useEffect(() => {
    const fetchTournamentDetails = async () => {
      if (!id) return;

      setLoading(true);
      setError(null);

      try {
        const response = await axios.get(
          `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}/tournament/${id}`
        );

        const tournamentData = response.data;
        setTournament(tournamentData);

        const currentDate = new Date();
        const deadlineDate = new Date(tournamentData.deadline);
        setRegistrationClosed(currentDate > deadlineDate);

        if (isAuthenticated) {

            // Check if player is already registered
            const registeredResponse = await axiosInstance.get(
              `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}/tournament/${id}/players`
            );
    
            const isRegistered = registeredResponse.data.some(
              (player: { user: User }) => {
                console.log(player.user.userId);
                console.log(userId);
    
                return player.user.userId === userId;
              }
            );

            setRegistered(isRegistered);
        }

      } catch (error) {
        console.error("Error fetching tournament details:", error);
        setError("Failed to load tournament details. Please try again.");

        if (axios.isAxiosError(error)) {
          handleError(error);
        }
      } finally {
        setLoading(false);
      }
    };

    fetchTournamentDetails();
  }, [id]);

  const onRegisterToggle = async () => {
    try {
      if (!userId) return; // Ensure userId exists

      if (!registered) {
        await axiosInstance.put(
          `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}/tournament/${id}/players/${userId}`
        );
      } else {
        await axiosInstance.delete(
          `${process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL}/tournament/${id}/players/${userId}`
        );
      }
      setRegistered(!registered);
    } catch (error) {
      console.error("Error toggling registration:", error);
    }
  };

  if (loading) return <p className="text-lg text-gray-500">Loading...</p>;
  if (error) return <p className="text-lg text-red-500">Error: {error}</p>;
  if (!tournament)
    return <p className="text-lg text-gray-500">Tournament not found.</p>;

  return (
    <div className="flex flex-col items-center min-h-screen pt-20">
      <header className="bg-background/10 w-full py-4 text-center text-white">
        <h1 className="text-3xl font-bold text-center">{tournament.name}</h1>
      </header>

      <p className="text-xl text-center">{`${new Date(tournament.startDate).toLocaleDateString("en-GB", { day: "2-digit", month: "long", year: "numeric" })} - ${new Date(tournament.endDate).toLocaleDateString("en-GB", { day: "2-digit", month: "long", year: "numeric" })}`}</p>

      <div className="mt-8 max-w-xl w-full">
        <h2 className="text-lg font-semibold">Location:</h2>
        <p className="text-base">{tournament.location}</p>
        <h2 className="mt-4 text-lg font-semibold">Description:</h2>
        <p className="text-base">{tournament.description}</p>
        <h2 className="mt-4 text-lg font-semibold">Registration Deadline:</h2>
        <p className="text-base">
          {new Date(tournament.deadline).toLocaleDateString("en-GB", {
            day: "2-digit",
            month: "long",
            year: "numeric",
          })}
        </p>

        {isAuthenticated && (
          <div className="flex justify-center w-full">
            <Button
              variant={registrationClosed || registered ? "outline" : "default"}
              disabled={registrationClosed}
              className="mt-4 flex justify-center w-auto"
              onClick={onRegisterToggle}
            >
              {registered ? "Withdraw" : "Register Now"}
            </Button>
          </div>
        )}
      </div>

      <div className="mt-4">
        <hr className="w-full my-4 border-t border-gray-300" />
        <h2 className="text-2xl font-semibold my-8 text-center">
          Tournament Draw
        </h2>
        <p className="text-center text-gray-500">Draw has yet to be released</p>
      </div>
    </div>
  );
};

export default withAuth(TournamentDetails, {
  requireAuth: false,
});
