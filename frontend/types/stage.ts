import { Round } from "./round";

export type Stage = {
    stageId: string;
    status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED';
    rounds: Round[];
}