
export interface Tournament {
    name: string;
    location: string;
    startDate: string;
    startTime: string;
    endDate: string;
    endTime: string;
    status: 'open' | 'ongoing' | 'completed';
    deadlineDate: string;
    deadlineTime: string;
    maxParticipants: number;
    description: string;
  }
  
