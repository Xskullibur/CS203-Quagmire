"use client";
import React, { useEffect, useState } from "react";
import axios from "axios";
import { Button } from "@/components/ui/button";
import axiosInstance from "@/lib/axios";
import { useGlobalErrorHandler } from "@/app/context/ErrorMessageProvider";
import { useAuth } from "@/hooks/useAuth"; // Import the auth hook
import { useRouter } from "next/navigation"; // Import useRouter for navigation
import { getPlayerProfileById } from "@/hooks/tournamentDataManager";
import TournamentBracket from "@/components/tournaments/TournamentBracket";
import { Skeleton } from "@/components/ui/skeleton";
import TournamentDetailsSkeleton from "@/components/tournaments/TournamentDetailsSkeleton ";
import { toast } from "@/hooks/use-toast";

interface Tournament {
  name: string;
  startDate: string;
  endDate: string;
  description: string;
  deadline: string;
  location: string;
  photoUrl?: string;
  winnerId: string | null;
  status: "SCHEDULED" | "IN_PROGRESS" | "COMPLETED" | "CANCELLED";
}

const API_URL = process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL;

const TournamentDetails: React.FC<{ params: { id: string } }> = ({
  params,
}) => {
  const { id } = params;
  const { user } = useAuth(); // Get user information from useAuth
  const isAdmin = user?.role === "ADMIN"; // Check if the user is an admin
  const router = useRouter();
  const [tournament, setTournament] = useState<Tournament | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [registrationClosed, setRegistrationClosed] = useState<boolean>(false);
  const [winnerUsername, setWinnerUsername] = useState<string | null>(null);

  const { handleError } = useGlobalErrorHandler();

  const firebaseBaseURL =
    "https://firebasestorage.googleapis.com/v0/b/quagmire-smu.appspot.com/o/";

  useEffect(() => {
    const fetchTournamentDetails = async () => {
      if (!id) return;

      setLoading(true);

      try {
        const response = await axiosInstance.get(`${API_URL}/tournament/${id}`);
        const tournamentData = response.data;

        if (tournamentData.photoUrl) {
          tournamentData.photoUrl = `${firebaseBaseURL}${encodeURIComponent(tournamentData.photoUrl)}?alt=media`;
        }

        setTournament(tournamentData);
        
        if (tournamentData.winnerId != null) {
          const winnerProfile = await getPlayerProfileById(
            tournamentData.winnerId
          );
          setWinnerUsername(winnerProfile.username);
        }

        const currentDate = new Date();
        const deadlineDate = new Date(tournamentData.deadline);
        setRegistrationClosed(
          currentDate > deadlineDate || tournamentData.status !== "SCHEDULED"
        );
        
      } catch (error) {

        if (axios.isAxiosError(error)) {
          handleError(error);
        }

      } finally {
        setLoading(false);
      }
    };

    fetchTournamentDetails();
  }, [id]);

  if (loading)
    return (
      <TournamentDetailsSkeleton/>
    );

  if (!tournament)
    return <p className="text-lg text-gray-500">Tournament not found.</p>;

  // Function to handle "Start Tournament" button click
  const handleStartTournament = async () => {
    try {
      const response = await axiosInstance.put(
        `${API_URL}/tournament/${id}/start`
      );
      
      toast({
        variant: "success",
        title: "Success",
        description: `${tournament.name} has started`,
      });

      router.push(`/tournaments/${id}/brackets`);
    } catch (error) {

      if (axios.isAxiosError(error)) {
        handleError(error)
      }

      console.error("Error starting tournament:", error);
    }
  };

  return (
    <div className="flex flex-col items-center min-h-screen pt-20">
      <header className="bg-background/10 w-full py-4 text-center text-white">
        <h1 className="text-3xl font-bold text-center">{tournament.name}</h1>
      </header>

      {tournament.photoUrl && (
        <img
          src={tournament.photoUrl}
          alt={`${tournament.name} photo`}
          className="w-full max-w-md mt-6 mb-4 rounded-lg shadow-lg object-cover"
        />
      )}

      <p className="text-xl text-center">{`${new Date(tournament.startDate).toLocaleDateString("en-GB", { day: "2-digit", month: "long", year: "numeric" })} - ${new Date(tournament.endDate).toLocaleDateString("en-GB", { day: "2-digit", month: "long", year: "numeric" })}`}</p>
      {/* Display Winner Information */}
      {tournament.winnerId && winnerUsername && (
        <div className="mt-8 text-center text-green-600 text-xl font-semibold">
          Winner: {winnerUsername}
        </div>
      )}
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

        {/* Register Tournament for Players Only */}
        {!isAdmin && tournament.status === "SCHEDULED" && (
          <div className="flex justify-center w-full">
            <Button
              variant={registrationClosed ? "outline" : "default"}
              disabled={registrationClosed}
              className="mt-4 flex justify-center w-auto"
            >
              {registrationClosed ? "Registration Closed" : "Register Now"}
            </Button>
          </div>
        )}

        {/* Start Tournament Button for Admins Only */}
        {isAdmin && tournament.status === "SCHEDULED" && (
          <div className="flex justify-center mt-6">
            <Button onClick={handleStartTournament}>Start Tournament</Button>
          </div>
        )}
      </div>

      <div className="m-4">
        <hr className="w-full my-4 border-t border-gray-300" />

        {/* Conditionally display message or button based on tournament status */}
        {(() => {
          if (
            tournament.status === "IN_PROGRESS" ||
            tournament.status === "COMPLETED"
          ) {
            return (
              <div className="relative overflow-x-auto">
                <div className="inline-block min-w-full">
                  <TournamentBracket tournamentId={id} />
                </div>
              </div>
            );
          } else if (tournament.status === "SCHEDULED") {
            return (
              <p className="text-center text-gray-500">
                Draw has yet to be released
              </p>
            );
          } else {
            return (
              <p className="text-center text-gray-500">
                Draw is not available for this tournament
              </p>
            );
          }
        })()}
      </div>
    </div>
  );
};

export default TournamentDetails;
