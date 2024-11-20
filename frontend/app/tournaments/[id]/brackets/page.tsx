// BracketsPage.tsx
"use client";
import React, { useState, useEffect } from "react";
import { useParams, useRouter } from "next/navigation";
import BracketMatch from "@/components/tournaments/BracketMatch";
import AutoAdvanceMatch from "@/components/tournaments/AutoAdvanceMatch";
import { Button } from "@/components/ui/button";
import { MatchTracker } from "@/types/matchTracker";
import { Input } from "@/components/ui/input";
import {
  getCurrentStageFromTournament,
  getMatchesForRound,
  getRoundsForTournamentAndStageId,
} from "@/hooks/tournamentDataManager";
import { useAuth } from "@/hooks/useAuth";
import { UserRole } from "@/types/user-role";
import axiosInstance from "@/lib/axios";
import axios from "axios";
import { useGlobalErrorHandler } from "@/app/context/ErrorMessageProvider";
import withAuth from "@/HOC/withAuth";

const API_URL = process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL;

const BracketsPage = () => {
  const { user } = useAuth();
  const isAdmin = user?.role === UserRole.ADMIN;
  const router = useRouter();
  const params = useParams();
  const tournamentId = Array.isArray(params.id)
    ? params.id[0]
    : params.id.toString();

  const [currentStageIndex, setCurrentStageIndex] = useState(0);
  const [roundIds, setRoundIds] = useState<string[]>([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [nextRoundEnabled, setNextRoundEnabled] = useState(false);
  const [matches, setMatches] = useState<MatchTracker[]>([]);
  const [actualMatches, setActualMatches] = useState<MatchTracker[]>([]);
  const [autoAdvanceMatches, setAutoAdvanceMatches] = useState<MatchTracker[]>(
    []
  );
  const [filteredActualMatches, setFilteredActualMatches] = useState<
    MatchTracker[]
  >([]);
  const [filteredAutoAdvanceMatches, setFilteredAutoAdvanceMatches] = useState<
    MatchTracker[]
  >([]);
  const [stageId, setStageId] = useState<string>("");
  const { handleError } = useGlobalErrorHandler();

  useEffect(() => {
    const initialiseTournament = async () => {
      try {
        const stageId = await getCurrentStageFromTournament(tournamentId);
        setStageId(stageId);
        const rounds = await getRoundsForTournamentAndStageId(
          tournamentId,
          stageId
        );
        if (rounds.length > 0) {
          const roundIdsArray = rounds.map((round: any) => round.roundId);
          setRoundIds(roundIdsArray);
          const matches = await getMatchesForRound(
            rounds[rounds.length - 1].roundId
          );
          setCurrentStageIndex(rounds.length - 1);
          if (matches && Array.isArray(matches)) {
            if (matches && Array.isArray(matches)) {
              const matchesWithCompletion = matches.map((match) => ({
                ...match,
                completed: match.status === "COMPLETED",
              }));
              setMatches(matchesWithCompletion);
              filterMatches(matchesWithCompletion);
            }

            filterMatches(matches);
          }
        }
      } catch (error) {
        if (axios.isAxiosError(error)) {
          handleError(error);
        }

        console.error("Error fetching stage and matches:", error);
      }
    };
    initialiseTournament();
  }, [tournamentId]);

  const filterMatches = (matches: MatchTracker[]) => {
    const actual = matches.filter((match) => match.player2);
    const autoAdvance = matches.filter((match) => !match.player2);
    setActualMatches(actual);
    setAutoAdvanceMatches(autoAdvance);
    setFilteredActualMatches(actual);
    setFilteredAutoAdvanceMatches(autoAdvance);
  };

  const handleNextRound = async () => {
    let tempNextRoundId = null;

    if (currentStageIndex === roundIds.length - 1 && isAdmin) {
      try {
        //complete the current round, and get the roundIds
        const res = await axiosInstance.get(
          new URL(
            `/tournament/${tournamentId}/stage/${stageId}/round/${roundIds[currentStageIndex]}`,
            API_URL
          ).toString()
        );
        const curRoundStatus = res.data.status;
        if (curRoundStatus !== "COMPLETED") {
          await axiosInstance.put(
            new URL(
              `/tournament/${tournamentId}/stage/${stageId}/round/${roundIds[currentStageIndex]}/end`,
              API_URL
            ).toString()
          );
          const rounds = await getRoundsForTournamentAndStageId(
            tournamentId,
            stageId
          );
          const roundIdArr = rounds.map((round) => round.roundId);
          setRoundIds(roundIdArr);
          tempNextRoundId = roundIdArr[roundIdArr.length - 1];
        } else {
          console.log("Round already completed");
        }
      } catch (error) {
        if (axios.isAxiosError(error)) {
          handleError(error);
        }
        console.log("unable to complete round" + error);
      }

      if (matches.length === 1) {
        //make a post to complete tournament and redirect to winners page
        await completeTournament();
      }
    }
    await proceedToNextRound(tempNextRoundId);

    
  };

  async function completeTournament() {
    try {
      await axiosInstance.put(
        new URL(
          `/tournament/${tournamentId}/stage/${stageId}/round/${roundIds[currentStageIndex]}/end`,
          API_URL
        ).toString()
      );
      await axiosInstance.put(
        new URL(`/tournament/${tournamentId}/progress`, API_URL).toString()
      );
      router.push(`/tournaments/${tournamentId}`);
    } catch (error) {
      if (axios.isAxiosError(error)) {
        handleError(error);
      }
      console.log("failed to complete tournaemnt" + error);
    }
  }

  async function proceedToNextRound(tempNextRoundId:string) {
    try {
      const nextRoundId = tempNextRoundId !== null
        ? tempNextRoundId
        : roundIds[currentStageIndex + 1];
      const matches = await getMatchesForRound(nextRoundId);

      if (matches && Array.isArray(matches)) {
        setMatches(matches);
        filterMatches(matches);
        setCurrentStageIndex(currentStageIndex + 1);
      }
    } catch (error) {
      if (axios.isAxiosError(error)) {
        handleError(error);
      }

      console.error("Error fetching next round matches:", error);
    }
  }

  const handlePreviousRound = async () => {
    if (currentStageIndex > 0) {
      try {
        const previousRoundId = roundIds[currentStageIndex - 1];
        const matches = await getMatchesForRound(previousRoundId);
        if (matches && Array.isArray(matches)) {
          setMatches(matches);
          filterMatches(matches);
          setCurrentStageIndex(currentStageIndex - 1);
        }
      } catch (error) {
        if (axios.isAxiosError(error)) {
          handleError(error);
        }

        console.error("Error fetching previous round matches:", error);
      }
    }
  };

  const handleMatchComplete = (
    matchId: string,
    winner: { username: string; id: string }
  ) => {
    if (!isAdmin) {
      alert("Only administrators can update match results.");
      return;
    }

    const updatedMatches = matches.map((match) =>
      match.matchId === matchId
        ? { ...match, completed: true, winner: winner }
        : match
    );
    setMatches(updatedMatches);
    filterMatches(updatedMatches);
  };

  useEffect(() => {
    const allMatchesCompleted = actualMatches.every((match) => {
      console.log(match.matchId, match.completed);
      return match.completed;
    });
    console.log(allMatchesCompleted);
    setNextRoundEnabled(allMatchesCompleted);
  }, [matches]);

  useEffect(() => {
    const filteredActual = actualMatches.filter(
      (match) =>
        match.player1.username
          .toLowerCase()
          .includes(searchQuery.toLowerCase()) ||
        (match.player2 &&
          match.player2.username
            .toLowerCase()
            .includes(searchQuery.toLowerCase()))
    );
    const filteredAutoAdvance = autoAdvanceMatches.filter((match) =>
      match.player1.username.toLowerCase().includes(searchQuery.toLowerCase())
    );
    setFilteredActualMatches(filteredActual);
    setFilteredAutoAdvanceMatches(filteredAutoAdvance);
  }, [searchQuery, actualMatches, autoAdvanceMatches]);

  return (
    <div className="container w-10/12 mx-auto mt-12 px-4 py-8">
      <h1 className="text-3xl font-semibold mb-6 text-center">
        Round {currentStageIndex + 1}
      </h1>

      <div className="mb-4 text-center sm:flex sm:justify-end sm:mb-8">
        <Input
          type="text"
          placeholder="Search by player name"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className="w-48 mx-auto sm:mx-0"
        />
      </div>

      {/* Actual Matches Section */}
      <div>
        <h2 className="text-2xl font-semibold mb-4">Matches</h2>
        <div className="flex flex-wrap gap-4">
          {filteredActualMatches.map((match) => (
            <BracketMatch
              key={match.matchId}
              match={match}
              onMatchComplete={
                isAdmin
                  ? (winner) => handleMatchComplete(match.matchId, winner)
                  : undefined
              }
              isAdmin={isAdmin}
            />
          ))}
        </div>
      </div>

      {/* Auto-Advance Matches Section */}
      {filteredAutoAdvanceMatches.length > 0 && (
        <div className="mt-8">
          <h2 className="text-2xl font-semibold mb-4">Auto Advancements</h2>
          <div className="flex flex-wrap gap-4">
            {filteredAutoAdvanceMatches.map((match) => (
              <AutoAdvanceMatch key={match.matchId} player={match.player1} />
            ))}
          </div>
        </div>
      )}

      <div className="mt-8 text-center">
        <Button
          onClick={handlePreviousRound}
          disabled={currentStageIndex === 0}
          className="mr-4 bg-blue-500 hover:bg-blue-700"
        >
          Previous Stage
        </Button>
        <Button
          onClick={handleNextRound}
          disabled={
            !nextRoundEnabled ||
            (!isAdmin && currentStageIndex === roundIds.length - 1)
          }
          className={`mt-4 ${nextRoundEnabled ? "bg-green-500" : "bg-gray-300"}`}
        >
          {actualMatches.length === 1
            ? "Complete Tournament"
            : currentStageIndex === roundIds.length - 1
              ? "Start Next Stage"
              : "View Next Stage"}
        </Button>
      </div>
    </div>
  );
};

export default withAuth(BracketsPage, UserRole.ADMIN);
