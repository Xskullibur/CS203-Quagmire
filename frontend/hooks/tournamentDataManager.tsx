import { MatchTracker } from "@/types/matchTracker";
import { MatchDTO } from "@/types/matchDTO";
import axios from "axios";

const API_URL = process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL;

// Function to get player profile by ID
const getPlayerProfileById = async (id: string): Promise<any> => {
    try {
        const response = await axios.get(`${API_URL}/profile/player/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Error fetching player profile for ID: ${id}`, error);
        throw error;
    }
};


const convertMatchToMatchTracker = async (match: any): Promise<MatchTracker> => {
    try {
        // Fetch player profiles for player1 and player2
        const player1Profile = await getPlayerProfileById(match.player1Id);
        const player2Profile = match.player2Id ? await getPlayerProfileById(match.player2Id) : null;
        const winnerProfile = match.winnerId? await getPlayerProfileById(match.winnerId) : null;

        // Create the MatchTracker object
        const matchTracker: MatchTracker = {
            matchId: match.id,
            player1: {
                username: player1Profile.username,
                id: match.player1Id,
                score: match.score.split("-")[0],
            },
            player2: player2Profile
                ? {
                    username: player2Profile.username,
                    id: match.player2Id,
                    score: match.score.split("-")[1],
                }
                : null,
            winner: match.winnerId
                ? {
                    username: winnerProfile.username,
                    id: match.winnerId,
                }
                : undefined,
            completed: match.status === 'COMPLETED',
        };

        return matchTracker;
    } catch (error) {
        console.error('Error converting match to MatchTracker:', error);
        throw error;
    }
};

// Convert MatchTracker to MatchDTO
const convertToMatchDTO = (match: MatchTracker): MatchDTO => {
    const score = match.player2 ? `${match.player1.score}-${match.player2.score}` : "0-0";
    const matchDTO: MatchDTO =  {
        player1Id: match.player1.id,
        player2Id: match.player2 ? match.player2.id : null,
        scheduledTime: null,
        winnerId: match.winner ? match.winner.id : null,
        score,
        meetingLatitude: null,
        meetingLongitude: null,
    };
    return matchDTO;
};

const getCurrentStageFromTournament = async (tournamentId: string) => {
    try {
        const stagesResponse = await axios.get(`${API_URL}/tournament/${tournamentId}/stage/allStages`);
        const stages = stagesResponse.data;
        if (!stages.length) {
            console.log('No stages found for this tournament.');
            return [];
        } else {
            return stages[0].stageId;
        }
    } catch (error) {
        console.error('Error fetching matches:', error);
        throw error;
    }
};

const getRoundsForTournamentAndStageId = async (tournamentId: string, stageId: string): Promise<any[]> => {
    try {
        const roundsResponse = await axios.get(`${API_URL}/tournament/${tournamentId}/stage/${stageId}/round/allRounds`);
        const rounds = roundsResponse.data;
        return rounds;
    } catch (error) {
        console.error("Error fetching rounds", error);
        throw error;
    }
}

const getMatchesForRound = async (roundId: string) => {
    try {
        const matchesResponse = await axios.get(`${API_URL}/match/round/${roundId}`);
        const matches = matchesResponse.data;
        const result = await Promise.all(matches.map((match: any) => convertMatchToMatchTracker(match)));
        return result;
    } catch (error) {
        console.error("Failed to fetch matches", error);
        throw error;
    }
}

const getProfileFromUsername = async (username: string) => {
    try {
        const response = await axios.get(`${API_URL}/profile/player/${username}`);
        const profileId = response.data.profileId;
        return profileId;
    } catch (error) {
        console.error("Failed to fetch profileId", error);
        throw error;
    }
}

export { getCurrentStageFromTournament, getMatchesForRound, getRoundsForTournamentAndStageId, getProfileFromUsername, convertToMatchDTO, getPlayerProfileById }
