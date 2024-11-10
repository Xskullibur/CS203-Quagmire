export interface Match {
    matchId: string;
    player1Id: string;
    player2Id: string;
    refereeId?: string;
    scheduledTime: string;
    status: 'SCHEDULED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
    winnerId?: string;
    score?: string;
    createdAt: string;
    updatedAt: string;
    gameType: 'SOLO' | 'TOURNAMENT';
    meetingLatitude: number;
    meetingLongitude: number;
  }
