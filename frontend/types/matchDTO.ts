export type MatchDTO = {
  player1Id: string;
  player2Id: string | null;
  scheduledTime: string | null;
  winnerId: string | null;
  score: string;
  meetingLatitude: number | null;
  meetingLongitude: number | null;
};
