'use client'
import React, { useState, useEffect } from "react";
import { useParams } from "next/navigation";
import BracketMatch from "@/components/tournaments/BracketMatch";
import AutoAdvanceMatch from "@/components/tournaments/AutoAdvanceMatch";
import { Button } from "@/components/ui/button";
import { MatchTracker } from "@/types/matchTracker";
import { Input } from "@/components/ui/input";
import { getCurrentStageFromTournament, getMatchesForRound, getRoundsForTournamentAndStageId } from "@/hooks/tournamentDataManager";
import { useAuth } from "@/hooks/useAuth";
import { UserRole } from "@/models/user-role";
import withAuth from "@/hooks/withAuth";

const BracketsPage = () => {
  const { user, isAuthenticated } = useAuth();
  const isAdmin = user?.role === UserRole.ADMIN;

  const id = (Array.isArray(useParams().id) ? useParams().id[0] : useParams().id).toString();

  const [currentStageIndex, setCurrentStageIndex] = useState(0);
  const [roundIds, setRoundIds] = useState<string[]>([]); // Array to keep track of round IDs
  const [searchQuery, setSearchQuery] = useState("");
  const [nextRoundEnabled, setNextRoundEnabled] = useState(false);
  const [matches, setMatches] = useState<MatchTracker[]>([]);
  const [filteredMatches, setFilteredMatches] = useState(matches);

  useEffect(() => {
    const initialiseTournament = async () => {
      try {
        const currentStage = await getCurrentStageFromTournament(id);
        const rounds = await getRoundsForTournamentAndStageId(id, currentStage.id);
        if (rounds.length > 0) {
          const roundIdsArray = rounds.map((round: any) => round.id);
          setRoundIds(roundIdsArray);
          const matches = await getMatchesForRound(rounds[rounds.length - 1].id);
          if (matches && Array.isArray(matches)) {
            setMatches(matches);
            setFilteredMatches(matches);
          }
        }
      } catch (error) {
        console.error('Error fetching stage and matches:', error);
      }
    };
    initialiseTournament();
  }, [id]);

  const handleNextRound = async () => {
    if (!isAdmin) return; // Only allow admin to proceed to next round
    if (currentStageIndex < roundIds.length - 1) {
      try {
        const nextRoundId = roundIds[currentStageIndex + 1];
        const matches = await getMatchesForRound(nextRoundId);
        if (matches && Array.isArray(matches)) {
          setMatches(matches);
          setFilteredMatches(matches);
          setCurrentStageIndex(currentStageIndex + 1);
        }
      } catch (error) {
        console.error('Error fetching next round matches:', error);
      }
    }
  };

  const handlePreviousRound = async () => {
    if (currentStageIndex > 0) {
      try {
        const previousRoundId = roundIds[currentStageIndex - 1];
        const matches = await getMatchesForRound(previousRoundId);
        if (matches && Array.isArray(matches)) {
          setMatches(matches);
          setFilteredMatches(matches);
          setCurrentStageIndex(currentStageIndex - 1);
        }
      } catch (error) {
        console.error('Error fetching previous round matches:', error);
      }
    }
  };

  const handleMatchComplete = (index: number, winner: { userId: string; id: string }) => {
    if (!isAdmin) {
      alert("Only administrators can update match results.");
      return;
    }

    const updatedMatches = matches.map((match, i) =>
      i === index ? { ...match, completed: true, winner: winner } : match
    );
    setMatches(updatedMatches);
    setFilteredMatches(updatedMatches);
  };

  useEffect(() => {
    const allMatchesCompleted = matches.every(
      (match) => match.completed || !match.player2
    );
    setNextRoundEnabled(allMatchesCompleted);
  }, [matches]);

  useEffect(() => {
    const filtered = matches.filter(
      (match) =>
        match.player1.userId.toLowerCase().includes(searchQuery.toLowerCase()) ||
        (match.player2 && match.player2.userId.toLowerCase().includes(searchQuery.toLowerCase()))
    );
    setFilteredMatches(filtered);
  }, [searchQuery, matches]);

  return (
    <div className="container w-10/12 mx-auto mt-12 px-4 py-8">
      <h1 className="text-3xl font-semibold mb-6 text-center">Round {currentStageIndex + 1}</h1>

      <div className="mb-4 text-center sm:flex sm:justify-end sm:mb-8">
        <Input
          type="text"
          placeholder="Search by player name"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className="w-48 mx-auto sm:mx-0"
        />
      </div>

      <div className="flex flex-wrap gap-4">
        {filteredMatches.map((match, index) => (
          match.player2 ? (
            <BracketMatch
              key={index}
              player1={match.player1}
              player2={match.player2}
              onMatchComplete={isAdmin ? (winner) => handleMatchComplete(index, winner) : undefined} // Only allow admin to update match results
            />
          ) : (
            <AutoAdvanceMatch
              key={index}
              player={match.player1}
            />
          )
        ))}
      </div>

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
          disabled={!nextRoundEnabled || currentStageIndex === roundIds.length - 1 || !isAdmin} // Only allow admin to proceed to next stage
          className={`mt-4 ${nextRoundEnabled ? "bg-green-500" : "bg-gray-300"}`}
        >
          Next Stage
        </Button>
      </div>
    </div>
  );
};

export default withAuth(BracketsPage, UserRole.ADMIN);
