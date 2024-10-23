'use client'
import React, { useState, useEffect } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPencilAlt } from "@fortawesome/free-solid-svg-icons"; // Import the pencil icon
import {
  Card,
  CardHeader,
  CardTitle,
  CardContent,
  CardFooter,
} from "@/components/ui/card"; // Adjust the import according to your project structure
import { Button } from "@/components/ui/button"; // Assuming you have a Button component

type Player = {
  name: string;
  score: number;
};

type MatchProps = {
  player1: Player;
  player2?: Player | null; // player2 is optional or can be null
  onMatchComplete?: (winner: string) => void; // Add onMatchComplete as a prop
};

const BracketMatch: React.FC<MatchProps> = ({ player1, player2, onMatchComplete }) => {
  const [p1Score, setP1Score] = useState(0);
  const [p2Score, setP2Score] = useState(0);
  const [winner, setWinner] = useState<string | null>(null);
  const [isEditing, setIsEditing] = useState(false); // State to handle edit mode

  // Handle score increment and winner determination
  const handleRoundWin = (player: "player1" | "player2") => {
    if (player === "player1") {
      setP1Score((prevScore) => {
        const newScore = prevScore + 1;
        if (newScore === 2) {
          setWinner(player1.name);
        }
        return newScore;
      });
    } else {
      setP2Score((prevScore) => {
        const newScore = prevScore + 1;
        if (newScore === 2) {
          setWinner(player2!.name); // player2 will always exist if this runs
        }
        return newScore;
      });
    }
  };

  // Ensure only valid inputs (0, 1, 2) are allowed
  const handleScoreChange = (e: React.ChangeEvent<HTMLInputElement>, setScore: React.Dispatch<React.SetStateAction<number>>) => {
    const value = Number(e.target.value);
    if (value >= 0 && value <= 2) {
      setScore(value);
    }
  };

  // Handle edit toggle
  const toggleEdit = () => {
    if (isEditing) {
      // If exiting edit mode, check if there's a winner based on the scores
      if (p1Score === 2 && p2Score === 2) {
        //make the method here
        alert("Both Player 1 and Player 2 cannot have a score of 2 at the same time.");
        return;
      }
      if (p1Score === 2) {
        setWinner(player1.name);
      } else if (p2Score === 2 && player2) {
        setWinner(player2.name);
      } else {
        setWinner(null); // Reset winner if scores are less than 2
      }
    } else {
      setWinner(null); // Reset winner when entering edit mode
    }

    setIsEditing(!isEditing);
  };

  useEffect(() => {
    if (winner && onMatchComplete) {
      onMatchComplete(winner); // Notify parent that match is complete
    }
  }, [winner]); // Only call when `winner` changes
  
  return (
    <Card className="mx-auto">
      <CardHeader className="flex flex-row justify-between items-center gap-4">
        <CardTitle>Tournament Match</CardTitle>
        {/* Pencil icon wrapped inside a button */}
        <button
          onClick={toggleEdit}
          className={`${isEditing ? "text-blue-500" : "text-gray-600"
            } hover:text-gray-300 transition-colors duration-200`}
        >
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

          {/* Only show "VS" and player2 if player2 exists */}
          {player2 ? (
            <>
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
            </>
          ) : (
            <p className="text-center text-green-600">Auto Advance</p>
          )}
        </div>

        {winner && player2 ? (
          <div className="text-center text-xl font-semibold text-green-600">
            Winner: {winner}
          </div>
        ) : (
          player2 && (
            <div className="text-center text-sm text-gray-500">
              Select the winner for each round.
            </div>
          )
        )}
      </CardContent>

      {/* Only show buttons if player2 exists */}
      {player2 && (
        <CardFooter className="space-x-4 flex place-content-center">
          <div className="flex flex-col space-y-2 sm:flex-row sm:gap-4 sm:space-y-0">
            <Button
              onClick={() => handleRoundWin("player1")}
              disabled={p1Score === 2 || winner !== null || isEditing}
            >
              {player1.name} Wins Round
            </Button>
            <Button
              onClick={() => handleRoundWin("player2")}
              disabled={p2Score === 2 || winner !== null || isEditing}
            >
              {player2.name} Wins Round
            </Button>
          </div>
        </CardFooter>
      )}
    </Card>
  );
};

export default BracketMatch;
