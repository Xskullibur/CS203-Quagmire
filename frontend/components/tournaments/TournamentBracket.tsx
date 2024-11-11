import React, { useEffect, useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Badge } from "@/components/ui/badge";
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip";
import { Trophy } from "lucide-react";
import { getPlayerProfileById } from "@/hooks/tournamentDataManager";
import { PlayerProfile } from "@/types/player-profile";
import { MatchDTO } from "@/types/matchDTO";
import { Stage } from "@/types/stage";
import axios from "axios";
import { Round } from '@/types/round';

const useMatchPlayers = (match: MatchDTO) => {
  const [players, setPlayers] = useState<{
    player1: PlayerProfile | null;
    player2: PlayerProfile | null;
  }>({
    player1: null,
    player2: null,
  });
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchPlayers = async () => {
      try {
        // Always fetch player1
        const player1Promise = getPlayerProfileById(match.player1Id);
        // Only fetch player2 if they exist
        const player2Promise = match.player2Id 
          ? getPlayerProfileById(match.player2Id)
          : Promise.resolve(null);

        const [p1, p2] = await Promise.all([player1Promise, player2Promise]);
        setPlayers({ player1: p1, player2: p2 });
      } catch (err) {
        setError("Failed to load players");
        console.error(err);
      } finally {
        setIsLoading(false);
      }
    };

    fetchPlayers();
  }, [match.player1Id, match.player2Id]);

  return { players, isLoading, error };
};

const MatchCard = ({ match }: { match: MatchDTO }) => {
  const { players, isLoading, error } = useMatchPlayers(match);
  const isAutoWin = !match.player2Id;
  
  const player1IsWinner = isAutoWin || match.winnerId === match.player1Id;
  const player2IsWinner = !isAutoWin && match.winnerId === match.player2Id;

  if (isLoading) {
    return <Skeleton className="h-32 w-full" />;
  }

  if (error) {
    return (
      <Card className="w-full min-w-[250px] bg-red-50">
        <CardContent className="p-4 text-red-500">
          Failed to load match data
        </CardContent>
      </Card>
    );
  }

  if (isAutoWin) {
    return (
      <Card className="w-full min-w-[250px] transition-all duration-200 hover:shadow-lg bg-success-foreground">
        <CardContent className="p-4">
          <div className="flex flex-col items-center space-y-2">
            <Badge variant="success" className="mb-2">
              Auto Advancement
            </Badge>
            <div className="flex items-center gap-2">
              <span className="font-medium">{players.player1?.username}</span>
              <Trophy className="h-4 w-4 text-yellow-500" />
            </div>
          </div>
          {match.scheduledTime && (
            <div className="mt-2 text-xs text-gray-500 dark:text-gray-400 text-center">
              {new Date(match.scheduledTime).toLocaleDateString()}
            </div>
          )}
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className="w-full min-w-[250px] transition-all duration-200 hover:shadow-lg">
      <CardContent className="p-4">
        {/* Player 1 */}
        <div
          className={`flex justify-between items-center p-2 rounded-t ${
            player1IsWinner ? "bg-success-foreground " : ""
          }`}
        >
          <div className="flex items-center gap-2">
            <span className="font-medium">{players.player1?.username ?? "TBD"}</span>
            {player1IsWinner && (
              <TooltipProvider>
                <Tooltip>
                  <TooltipTrigger>
                    <Trophy className="h-4 w-4 text-yellow-500" />
                  </TooltipTrigger>
                  <TooltipContent>Winner</TooltipContent>
                </Tooltip>
              </TooltipProvider>
            )}
          </div>
          <Badge
            variant={player1IsWinner ? "success" : "secondary"}
            className="ml-2"
          >
            {match.score?.split("-")[0] ?? "-"}
          </Badge>
        </div>

        {/* Divider */}
        <div className="h-px bg-gray-200 dark:bg-gray-800 my-2" />

        {/* Player 2 */}
        <div
          className={`flex justify-between items-center p-2 rounded-b ${
            player2IsWinner ? "bg-success-foreground" : ""
          }`}
        >
          <div className="flex items-center gap-2">
            <span className="font-medium">{players.player2?.username ?? "TBD"}</span>
            {player2IsWinner && (
              <TooltipProvider>
                <Tooltip>
                  <TooltipTrigger>
                    <Trophy className="h-4 w-4 text-yellow-500" />
                  </TooltipTrigger>
                  <TooltipContent>Winner</TooltipContent>
                </Tooltip>
              </TooltipProvider>
            )}
          </div>
          <Badge
            variant={player2IsWinner ? "success" : "secondary"}
            className="ml-2"
          >
            {match.score?.split("-")[1] ?? "-"}
          </Badge>
        </div>

        {/* Match Date */}
        {match.scheduledTime && (
          <div className="mt-2 text-xs text-gray-500 dark:text-gray-400 text-center">
            {new Date(match.scheduledTime).toLocaleDateString()}
          </div>
        )}
      </CardContent>
    </Card>
  );
};

const LoadingSkeleton = () => (
  <div className="flex gap-8 overflow-x-auto p-4">
    {[1, 2, 3, 4].map((i) => (
      <div key={i} className="flex flex-col gap-4">
        <Skeleton className="h-6 w-32" />
        <div className="flex flex-col gap-4">
          {[1, 2].map((j) => (
            <Skeleton key={j} className="h-32 w-64" />
          ))}
        </div>
      </div>
    ))}
  </div>
);

const TournamentBracket = ({ tournamentId }: { tournamentId: string }) => {
  const [stages, setStages] = useState<Stage[]>([]);
  const [loading, setLoading] = useState(true);

  const API_URL = process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL;

  useEffect(() => {
    const fetchTournamentData = async () => {
      try {
        const stagesResponse = await axios.get(
          `${API_URL}/tournament/${tournamentId}/stage/allStages`
        );

        const stagesData = stagesResponse.data;

        const stagesWithDetails = await Promise.all(
          stagesData.map(async (stage: Stage) => {
            const stageDetails = await axios.get(
              `${API_URL}/tournament/${tournamentId}/stage/${stage.stageId}`
            );
            return { 
              ...stage, 
              rounds: [...stageDetails.data.rounds].sort((a, b) => a.roundNumber - b.roundNumber)
            };
          })
        );
        
        setStages(stagesWithDetails);
        setLoading(false);
      } catch (error) {
        console.error("Error fetching tournament data:", error);
        setLoading(false);
      }
    };

    fetchTournamentData();
  }, [tournamentId]);

  if (loading) {
    return <LoadingSkeleton />;
  }

  // Helper function to calculate match position
  const calculateMatchPosition = (matchIndex: number, roundIndex: number, isAutoAdvance: boolean, previousMatches: MatchDTO[]) => {
    const baseSpacing = 160; // Increased base spacing for regular matches
    const autoAdvanceSpacing = 120; // Original spacing for auto-advance matches
    const spacingMultiplier = Math.pow(2, roundIndex);
    
    let position = 0;
    
    // For first round matches
    if (roundIndex === 0) {
      let adjustedIndex = 0;
      for (let i = 0; i < matchIndex; i++) {
        // Add appropriate spacing based on whether previous match was auto-advance
        const prevMatchAutoAdvance = !previousMatches[i].player2Id;
        adjustedIndex += prevMatchAutoAdvance ? (autoAdvanceSpacing / baseSpacing) : 1;
      }
      position = adjustedIndex * baseSpacing;
    } else {
      // For subsequent rounds, use regular spacing
      position = matchIndex * baseSpacing * spacingMultiplier;
    }
    
    return position;
  };

  return (
    <Card className="w-full bg-background">
      <CardHeader>
        <CardTitle className="text-center">Tournament Draw</CardTitle>
      </CardHeader>
      <CardContent className="overflow-x-auto">
        <div className="flex min-w-fit p-4 relative">
          {stages[0]?.rounds.map((round: Round, roundIndex: number) => (
            <div 
              key={round.roundId} 
              className="flex flex-col"
              style={{
                marginLeft: roundIndex > 0 ? '4rem' : '0',
                width: '280px'
              }}
            >
              <div className="text-center mb-8">
                <h3 className="text-sm font-medium text-muted-foreground mb-2">
                  Round {roundIndex + 1}
                </h3>
                <Badge
                  variant={
                    round.status === "COMPLETED"
                      ? "success"
                      : round.status === "IN_PROGRESS"
                        ? "secondary"
                        : "outline"
                  }
                >
                  {round.status}
                </Badge>
              </div>

              <div className="flex flex-col relative">
                {round.matches.map((match: MatchDTO, matchIndex: number) => {
                  const isAutoAdvance = !match.player2Id;
                  const verticalPosition = calculateMatchPosition(
                    matchIndex,
                    roundIndex,
                    isAutoAdvance,
                    round.matches
                  );

                  return (
                    <div 
                      key={`${match.scheduledTime}-${matchIndex}`} 
                      className="absolute w-full"
                      style={{
                        top: verticalPosition,
                      }}
                    >
                      <MatchCard match={match} />
                      
                      {/* Connector lines */}
                      {roundIndex < stages[0].rounds.length - 1 && (
                        <>
                          {/* Horizontal connector */}
                          <div 
                            className="absolute left-full top-1/2 w-16 border-t-2 border-gray-600"
                          />
                          
                          {/* Vertical connector for pairs */}
                          {matchIndex % 2 === 0 && (
                            <div 
                              className="absolute left-full top-1/2 border-r-2 border-gray-600"
                              style={{
                                height: isAutoAdvance 
                                  ? `${120 * Math.pow(2, roundIndex)}px`
                                  : `${160 * Math.pow(2, roundIndex)}px`,
                                transform: 'translateX(4rem)',
                              }}
                            />
                          )}
                        </>
                      )}
                    </div>
                  );
                })}
              </div>
              
              {/* Container to ensure proper round height */}
              <div style={{
                height: `${round.matches.length * Math.pow(2, roundIndex) * 160}px`
              }} />
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  );
};

export default TournamentBracket;