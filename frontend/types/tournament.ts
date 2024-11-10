
export interface Tournament {
    id: string | null;
    name: string;
    location: string;
    startDate: string;
    startTime: string;
    endDate: string;
    endTime: string;
    status: 'SCHEDULED'| 'INPROGRESS' | 'COMPLETED' | 'CANCELLED';
    deadline: string;
    deadlineTime: string;
    maxParticipants: number;
    description: string;
    refereeIds: string[];
  }
