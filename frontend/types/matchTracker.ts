
export type MatchTracker = {
    matchId: string;
    player1: { userId: string; id: string; score: number };
    player2?: { userId: string; id: string; score: number } | null;
    winner?: { userId: string; id: string } | null;
    completed?: boolean;
  };
  