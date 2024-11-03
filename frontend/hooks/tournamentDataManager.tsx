import { MatchTracker } from "@/types/matchtracker";
import { playerIdentifier } from "@/types/playerIdentifier";
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

const convertMatchToMatchTracker = async (match: any): Promise<MatchTracker> => {
    try {
      // Fetch player profiles for player1 and player2
      const player1Profile = await getPlayerProfileById(match.player1Id);
      const player2Profile = match.player2Id ? await getPlayerProfileById(match.player2Id) : null;

  
      // Create the MatchTracker object
      const matchTracker: MatchTracker = {
        player1: {
          userId: player1Profile.userId,
          id: match.player1Id,
          score: 0,
        },
        player2: player2Profile
          ? {
              userId: player2Profile.userId,
              id: match.player2Id,
              score: 0,
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
    }

}

export { getCurrentStageFromTournament, getMatchesForRound, getRoundsForTournamentAndStageId }