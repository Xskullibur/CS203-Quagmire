
export type MatchTracker = {
    matchId: string;
    player1: { username: string; id: string; score: number };
    player2?: { username: string; id: string; score: number } | null;
    winner?: { username: string; id: string } | null;
    completed?: boolean;
  };
  