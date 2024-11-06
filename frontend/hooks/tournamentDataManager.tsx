import { MatchTracker } from "@/types/matchTracker";
import { playerIdentifier } from "@/types/playerIdentifier";
import { MatchDTO } from "@/types/matchDTO";
import axios from "axios";


// Function to get player profile by ID
const getPlayerProfileById = async (id: string): Promise<playerIdentifier> => {
    try {
        const response = await axios.get<playerIdentifier>(`/player/${id}`);
        return response.data;
    } catch (error) {
        console.error(`Error fetching player profile for ID: ${id}`, error);
        throw error;
    }
};

const getRefereeIds = async (id: string): Promise<Set<string>> => {
    try {
        const response = await axios.get(`/tournament/DTO/${id}`);
        const refereeIds = response.data.refereeIds;
        return refereeIds;
    } catch (error) {
        console.error("Error fetching refereeIds");
        throw error;
    }
}

const convertMatchToMatchTracker = async (match: any): Promise<MatchTracker> => {
    try {
        // Fetch player profiles for player1 and player2
        const player1Profile = await getPlayerProfileById(match.player1Id);
        const player2Profile = match.player2Id ? await getPlayerProfileById(match.player2Id) : null;


        // Create the MatchTracker object
        const matchTracker: MatchTracker = {
            matchId: match.id,
            player1: {
                userId: player1Profile.userId,
                id: match.player1Id,
                score: match.score.split("-")[0],
            },
            player2: player2Profile
                ? {
                    userId: player2Profile.userId,
                    id: match.player2Id,
                    score: match.score.split("-")[1],
                }
                : null,
            winner: match.winnerId
                ? {
                    userId: match.winnerId,
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

//Convert MatchTracker to MatchDTO
const convertToMatchDTO = (match: MatchTracker): MatchDTO => {
    const score = match.player2? `${match.player1.score}-${match.player2.score}` : "0";
    return {
      player1Id: match.player1.id,
      player2Id: match.player2? match.player2.id : null,
      scheduledTime: null,
      winnerId: match.winner ? match.winner.id : null,
      score,
      meetingLatitude: null,
      meetingLongitude: null,
    };
  };



const getCurrentStageFromTournament = async (tournamentId: string) => {
    try {
        // Step 1: Get all stages by tournament ID
        const stagesResponse = await axios.get(`/tournament/${tournamentId}/stage/allStages`);
        const stages = stagesResponse.data; // Array of Stage objects

        if (!stages.length) {
            console.log('No stages found for this tournament.');
            return [];
        } else {
            return stages[stages.length - 1].stageId;
        }

    } catch (error) {
        console.error('Error fetching matches:', error);
        throw error;
    }
};

const getRoundsForTournamentAndStageId = async (tournamentId: string, stageId: string) => {
    try {
        const roundsResponse = await axios.get(`/tournament/${tournamentId}/stage/${stageId}/round/allRounds`);
        const rounds = roundsResponse.data; // Array of Round objects
        return rounds;
    } catch (error) {
        console.error("Error fetching rounds", error);
    }

}

const getMatchesForRound = async (roundId: string) => {

    try {

        const matchesResponse = await axios.get(`/match/round/${roundId}`);
        const matches = matchesResponse.data; // Array of Match objects
        const result = await Promise.all(matches.map((match: any) => convertMatchToMatchTracker(match)));
        return result;

    } catch (error) {
        console.error("Failed to fetch matches", error);
        throw error;
    }

}

const getProfileFromUsername = async (username: string) => {
    try {
        const response = await axios.get(`/profile/player/${username}`);
        const profileId = response.data.profileId;
        return profileId;
    }
    catch (error) {
        console.error("Failed to fetch profileId", error);
        throw error;
    }
}

export { getCurrentStageFromTournament, getMatchesForRound, getRoundsForTournamentAndStageId, getRefereeIds, getProfileFromUsername, convertToMatchDTO }