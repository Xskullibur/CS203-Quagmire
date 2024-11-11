import { PlayerProfile } from "./player-profile";

export type Match = {
    matchId: string;
    player1: string;
    player2?: string;
    winner?: string;
    completed: boolean;
    score?: string;
    date?: string;
}