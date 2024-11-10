// BracketsPage.tsx
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
import { UserRole } from "@/types/user-role";
import axiosInstance from "@/lib/axios";


const API_URL = process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL;


const BracketsPage = () => {
  const { user, isAuthenticated } = useAuth();
  const isAdmin = user?.role === UserRole.ADMIN;

  const tournamentId = (Array.isArray(useParams().id) ? useParams().id[0] : useParams().id).toString();

  const [currentStageIndex, setCurrentStageIndex] = useState(0);
  const [roundIds, setRoundIds] = useState<string[]>([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [nextRoundEnabled, setNextRoundEnabled] = useState(false);
  const [matches, setMatches] = useState<MatchTracker[]>([]);
  const [actualMatches, setActualMatches] = useState<MatchTracker[]>([]);
  const [autoAdvanceMatches, setAutoAdvanceMatches] = useState<MatchTracker[]>([]);
  const [filteredActualMatches, setFilteredActualMatches] = useState<MatchTracker[]>([]);
  const [filteredAutoAdvanceMatches, setFilteredAutoAdvanceMatches] = useState<MatchTracker[]>([]);
  const [stageId, setStageId] = useState<string>("");

  useEffect(() => {
    const initialiseTournament = async () => {
      try {
        const stageId = await getCurrentStageFromTournament(tournamentId);
        setStageId(stageId)
        const rounds = await getRoundsForTournamentAndStageId(tournamentId, stageId);
        if (rounds.length > 0) {
          const roundIdsArray = rounds.map((round: any) => round.roundId);
          setRoundIds(roundIdsArray);
          const matches = await getMatchesForRound(rounds[rounds.length - 1].roundId);
          if (matches && Array.isArray(matches)) {
            setMatches(matches);
            filterMatches(matches);
          }
        }
      } catch (error) {
        console.error('Error fetching stage and matches:', error);
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
    if (!isAdmin) return;
    console.log(currentStageIndex);
    try {

      const res = await axiosInstance.put(new URL(`/tournament/${tournamentId}/stage/${stageId}/round/${roundIds[currentStageIndex]}/end`, API_URL).toString());
      const rounds = await getRoundsForTournamentAndStageId(tournamentId, stageId);
      console.log(rounds);
      const roundIdArr = rounds.map((round) => round.roundId());
      setRoundIds(roundIdArr);
      const nextRoundId = roundIds[currentStageIndex + 1];
      const matches = await getMatchesForRound(nextRoundId);
      if (matches && Array.isArray(matches)) {
        setMatches(matches);
        filterMatches(matches);
        setCurrentStageIndex(currentStageIndex + 1);
      }
      console.log(currentStageIndex);

    } catch (error) {
      console.error('Error fetching next round matches:', error);
    }

  };

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
        console.error('Error fetching previous round matches:', error);
      }
    }
  };

  const handleMatchComplete = (index: number, winner: { username: string; id: string }) => {
    if (!isAdmin) {
      alert("Only administrators can update match results.");
      return;
    }

    const updatedMatches = matches.map((match, i) =>
      i === index ? { ...match, completed: true, winner: winner } : match
    );
    setMatches(updatedMatches);
    filterMatches(updatedMatches);
  };

  useEffect(() => {
    const allMatchesCompleted = actualMatches.every((match) => match.completed);
    setNextRoundEnabled(allMatchesCompleted);
  }, [actualMatches]);


  useEffect(() => {
    const filteredActual = actualMatches.filter(
      (match) =>
        match.player1.username.toLowerCase().includes(searchQuery.toLowerCase()) ||
        (match.player2 && match.player2.username.toLowerCase().includes(searchQuery.toLowerCase()))
    );
    const filteredAutoAdvance = autoAdvanceMatches.filter((match) =>
      match.player1.username.toLowerCase().includes(searchQuery.toLowerCase())
    );
    setFilteredActualMatches(filteredActual);
    setFilteredAutoAdvanceMatches(filteredAutoAdvance);
  }, [searchQuery, actualMatches, autoAdvanceMatches]);

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

      {/* Actual Matches Section */}
      <div>
        <h2 className="text-2xl font-semibold mb-4">Matches</h2>
        <div className="flex flex-wrap gap-4">
          {filteredActualMatches.map((match, index) => (
            <BracketMatch
              key={match.matchId}
              match={match}
              onMatchComplete={isAdmin ? (winner) => handleMatchComplete(index, winner) : undefined}
              isAdmin={isAdmin}
            />
          ))}
        </div>
      </div>

      {/* Auto-Advance Matches Section */}
      <div className="mt-8">
        <h2 className="text-2xl font-semibold mb-4">Auto Advancements</h2>
        <div className="flex flex-wrap gap-4">
          {filteredAutoAdvanceMatches.map((match, index) => (
            <AutoAdvanceMatch key={index} player={match.player1} />
          ))}
        </div>
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
          disabled={!nextRoundEnabled || (!isAdmin && currentStageIndex === roundIds.length - 1)}
          className={`mt-4 ${nextRoundEnabled ? "bg-green-500" : "bg-gray-300"}`}
        >
          {currentStageIndex === roundIds.length - 1 ? "Start Next Stage" : "View Next Stage"}
        </Button>
      </div>
    </div>
  );
};

export default BracketsPage;
