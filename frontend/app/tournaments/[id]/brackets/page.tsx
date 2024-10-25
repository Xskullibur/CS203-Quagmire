'use client'
import React, { useState, useEffect } from "react";
import BracketMatch from "@/components/tournaments/BracketMatch"; // Adjust import based on your file structure
import { Button } from "@/components/ui/button"; // Assuming you have a Button component
import { Input } from "@/components/ui/input";

type Match = {
  player1: { name: string; score: number };
  player2?: { name: string; score: number } | null; // Allow player2 to be null for auto advance
  winner?: string | null;
  completed?: boolean; // Track if the match has a winner
};

const stageData = {
  stageName: "Quarterfinals",
  matches: [
    {
      player1: { name: "Player 1", score: 0 },
      player2: { name: "Player 8", score: 0 },
      winner: null,
      completed: false,
    },
    {
      player1: { name: "Player 2", score: 0 },
      player2: { name: "Player 7", score: 0 },
      winner: null,
      completed: false,
    },
    {
      player1: { name: "Player 3", score: 0 },
      player2: { name: "Player 6", score: 0 },
      winner: null,
      completed: false,
    },
    {
      player1: { name: "Player 4", score: 0 },
      player2: { name: "Player 5", score: 0 },
      winner: null,
      completed: false,
    },
    {
      player1: {name: "superman", score:0},
      player2: null
    }
  ],
};

const BracketsPage = () => {
  const [matches, setMatches] = useState<Match[]>(stageData.matches);
  const [filteredMatches, setFilteredMatches] = useState<Match[]>(stageData.matches);
  const [searchQuery, setSearchQuery] = useState("");
  const [nextRoundEnabled, setNextRoundEnabled] = useState(false);

  // Function to handle match completion
  const handleMatchComplete = (index: number, winner: string) => {
    const updatedMatches = matches.map((match, i) =>
      i === index ? { ...match, completed: true, winner: winner } : match
    );
    console.log(updatedMatches);
    setMatches(updatedMatches);
  };

  // Check if all matches are completed (excluding auto-advance)
  useEffect(() => {
    const allMatchesCompleted = matches.every(
      (match) => match.completed || !match.player2 // auto-advance players don't count
    );
    setNextRoundEnabled(allMatchesCompleted);
  }, [matches]);

  // Filter matches based on the search query
  useEffect(() => {
    const filtered = matches.filter(
      (match) =>
        match.player1.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        (match.player2 && match.player2.name.toLowerCase().includes(searchQuery.toLowerCase()))
    );
    setFilteredMatches(filtered);
  }, [searchQuery, matches]);

  return (
    <div className="container w-10/12 mx-auto mt-12 px-4 py-8">
      <h1 className="text-3xl font-semibold mb-6 text-center">{stageData.stageName}</h1>

      {/* Search input field */}
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
          <BracketMatch
            key={index}
            player1={match.player1}
            player2={match.player2}
            onMatchComplete={(winner) => handleMatchComplete(index, winner)}
          />
        ))}
      </div>

      {/* "Next Round" button */}
      <div className="mt-8 text-center">
        <Button
          onClick={() => console.log("Proceed to next round")}
          disabled={!nextRoundEnabled}
          className={`mt-4 ${nextRoundEnabled ? "bg-green-500" : "bg-gray-300"}`}
        >
          Next Round
        </Button>
      </div>
    </div>
  );
};

export default BracketsPage;
