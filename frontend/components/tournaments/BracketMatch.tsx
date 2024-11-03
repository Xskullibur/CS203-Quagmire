// BracketMatch.tsx
'use client'
import React, { useState, useEffect } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPencilAlt } from "@fortawesome/free-solid-svg-icons";
import { Card, CardHeader, CardTitle, CardContent, CardFooter } from "@/components/ui/card";
import { Button } from "@/components/ui/button";

type Player = {
  name: string;
  id: string;
  score: number | null;
};

type MatchProps = {
  player1: Player;
  player2: Player;
  onMatchComplete?: (winner: { name: string; id: string }) => void;
};

const BracketMatch: React.FC<MatchProps> = ({ player1, player2, onMatchComplete }) => {
  const [p1Score, setP1Score] = useState(0);
  const [p2Score, setP2Score] = useState(0);
  const [winner, setWinner] = useState<{ name: string; id: string } | null>(null);
  const [isEditing, setIsEditing] = useState(false);

  // Handle winner determination based on score updates
  const handleRoundWin = (player: "player1" | "player2") => {
    if (player === "player1") {
      setP1Score((prev) => {
        const newScore = prev + 1;
        if (newScore === 2) setWinner({ name: player1.name, id: player1.id });
        return newScore;
      });
    } else {
      setP2Score((prev) => {
        const newScore = prev + 1;
        if (newScore === 2) setWinner({ name: player2.name, id: player2.id });
        return newScore;
      });
    }
  };

  // Allow only valid score inputs
  const handleScoreChange = (e: React.ChangeEvent<HTMLInputElement>, setScore: React.Dispatch<React.SetStateAction<number>>) => {
    const value = Number(e.target.value);
    if (value >= 0 && value <= 2) setScore(value);
  };

  // Toggle edit mode
  const toggleEdit = () => {
    if (isEditing && p1Score === 2 && p2Score === 2) {
      alert("Both players cannot have a score of 2 at the same time.");
      return;
    }
    setWinner(p1Score === 2 ? { name: player1.name, id: player1.id } : p2Score === 2 ? { name: player2.name, id: player2.id } : null);
    setIsEditing(!isEditing);
  };

  useEffect(() => {
    if (winner && onMatchComplete) onMatchComplete(winner);
  }, [winner]);

  return (
    <Card className="mx-auto">
      <CardHeader className="flex flex-row justify-between items-center gap-4">
        <CardTitle>Tournament Match</CardTitle>
        <button onClick={toggleEdit} className={`${isEditing ? "text-blue-500" : "text-gray-600"} hover:text-gray-300 transition-colors duration-200`}>
          <FontAwesomeIcon icon={faPencilAlt} className="w-5 h-5" />
        </button>
      </CardHeader>

      <CardContent>
        <div className="flex justify-between items-center mb-4">
          <div>
            <h4 className="text-lg font-semibold">{player1.name}</h4>
            {isEditing ? (
              <input
                type="number"
                value={p1Score}
                onChange={(e) => handleScoreChange(e, setP1Score)}
                className="border rounded p-1 w-16 text-center text-black"
                min="0"
                max="2"
              />
            ) : (
              <p>Score: {p1Score}</p>
            )}
          </div>

          <span className="font-bold text-xl">VS</span>

          <div>
            <h4 className="text-lg font-semibold">{player2.name}</h4>
            {isEditing ? (
              <input
                type="number"
                value={p2Score}
                onChange={(e) => handleScoreChange(e, setP2Score)}
                className="border rounded p-1 w-16 text-center text-black"
                min="0"
                max="2"
              />
            ) : (
              <p>Score: {p2Score}</p>
            )}
          </div>
        </div>

        {winner && (
          <div className="text-center text-xl font-semibold text-green-600">
            Winner: {winner.name}
          </div>
        )}
      </CardContent>

      <CardFooter className="space-x-4 flex justify-center">
        <Button onClick={() => handleRoundWin("player1")} disabled={p1Score === 2 || !!winner || isEditing}>
          {player1.name} Wins Round
        </Button>
        <Button onClick={() => handleRoundWin("player2")} disabled={p2Score === 2 || !!winner || isEditing}>
          {player2.name} Wins Round
        </Button>
      </CardFooter>
    </Card>
  );
};

export default BracketMatch;
