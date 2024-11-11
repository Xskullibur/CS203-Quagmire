import { Match } from "./match";
import { MatchDTO } from "./matchDTO";

export type Round = {
    roundId: string;
    roundNumber: number;
    status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED';
    matches: MatchDTO[];
}