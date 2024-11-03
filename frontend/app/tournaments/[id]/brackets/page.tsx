'use client'
import React, { useState, useEffect } from "react";
import BracketMatch from "@/components/tournaments/BracketMatch";
import AutoAdvanceMatch from "@/components/tournaments/AutoAdvanceMatch";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import axios from "axios";



type MatchTracker = {
  player1: { name: string; id: string; score: number };
  player2?: { name: string; id: string; score: number } | null;
  winner?: { name: string; id: string } | null;
  completed?: boolean;
};

const stages: { stageName: string; matches: MatchTracker[] }[] = [
  {
    stageName: "Quarterfinals",
    matches: [
      { player1: { name: "Player 1", id: "uuid1", score: 0 }, player2: { name: "Player 8", id: "uuid8", score: 0 }, winner: null, completed: false },
      { player1: { name: "Player 2", id: "uuid2", score: 0 }, player2: { name: "Player 7", id: "uuid7", score: 0 }, winner: null, completed: false },
      { player1: { name: "Player 3", id: "uuid3", score: 0 }, player2: { name: "Player 6", id: "uuid6", score: 0 }, winner: null, completed: false },
      { player1: { name: "Player 4", id: "uuid4", score: 0 }, player2: { name: "Player 5", id: "uuid5", score: 0 }, winner: null, completed: false },
    ],
  },
  {
    stageName: "Semifinals",
    matches: [
      // Define the matches for the semifinals here
    ],
  },
  {
    stageName: "Finals",
    matches: [
      // Define the matches for the finals here
    ],
  },
];



const BracketsPage = () => {
  

  const [currentStageIndex, setCurrentStageIndex] = useState(0);
  const [stageResults, setStageResults] = useState(stages);
  const [searchQuery, setSearchQuery] = useState("");
  const [nextRoundEnabled, setNextRoundEnabled] = useState(false);
  const [matches, setMatches] = useState<MatchTracker[]>(stages[currentStageIndex].matches);
  const [filteredMatches, setFilteredMatches] = useState(matches);


  useEffect(() => {
    setMatches(stages[currentStageIndex].matches);
    setFilteredMatches(stages[currentStageIndex].matches);
  }, [currentStageIndex]);

  useEffect(() => {
    setMatches(stageResults[currentStageIndex].matches);
    setFilteredMatches(stageResults[currentStageIndex].matches);
  }, [currentStageIndex, stageResults]);

  const handleMatchComplete = (index: number, winner: { name: string; id: string }) => {
    const updatedMatches = matches.map((match, i) =>
      i === index ? { ...match, completed: true, winner: winner } : match
    );
    const updatedStageResults = [...stageResults];
    updatedStageResults[currentStageIndex] = {
      ...updatedStageResults[currentStageIndex],
      matches: updatedMatches,
    };
    setStageResults(updatedStageResults);
    setMatches(updatedMatches);
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
        match.player1.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        (match.player2 && match.player2.name.toLowerCase().includes(searchQuery.toLowerCase()))
    );
    setFilteredMatches(filtered);
  }, [searchQuery, matches]);

  const handleNextRound = async () => {
    if (currentStageIndex < stages.length - 1) {
      setCurrentStageIndex(currentStageIndex + 1);
    }
  };

  const handlePreviousRound = () => {
    if (currentStageIndex > 0) {
      setCurrentStageIndex(currentStageIndex - 1);
    }
  };

  return (
    <div className="container w-10/12 mx-auto mt-12 px-4 py-8">
      <h1 className="text-3xl font-semibold mb-6 text-center">{stages[currentStageIndex].stageName}</h1>

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
              onMatchComplete={(winner) => handleMatchComplete(index, winner)}
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
          disabled={!nextRoundEnabled || currentStageIndex === stages.length - 1}
          className={`mt-4 ${nextRoundEnabled ? "bg-green-500" : "bg-gray-300"}`}
        >
          Next Stage
        </Button>
      </div>
    </div>
  );
};

export default BracketsPage;