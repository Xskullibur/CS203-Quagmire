import React from "react";
import BracketMatch from "@/components/tournaments/BracketMatch"; // Adjust import based on your file structure

// Simulate a stage with multiple matches
const stageData = {
  stageName: "Quarterfinals",
  matches: [
    {
      player1: { name: "Player 1", score: 0 },
      player2: { name: "Player 8", score: 0 },
    },
    {
      player1: { name: "Player 2", score: 0 },
      player2: { name: "Player 7", score: 0 },
    },
    {
      player1: { name: "Player 3", score: 0 },
      player2: { name: "Player 6", score: 0 },
    },
    {
      player1: { name: "Player 4", score: 0 },
      player2: { name: "Player 5", score: 0 },
    },
  ],
};

const BracketsPage = () => {
  return (
    <div className="container mx-auto mt-12 px-4 py-8">
      <h1 className="text-3xl font-semibold mb-6 text-center">
        {stageData.stageName}
      </h1>
      {/* Grid Layout: 1 column for mobile, 2 columns for medium screens and up */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {stageData.matches.map((match, index) => (
          <BracketMatch
            key={index}
            player1={match.player1}
            player2={match.player2}
          />
        ))}
      </div>
    </div>
  );
};

export default BracketsPage;
