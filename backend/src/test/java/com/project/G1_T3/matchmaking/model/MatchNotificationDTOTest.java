package com.project.G1_T3.matchmaking.model;

import org.junit.jupiter.api.Test;

import com.project.G1_T3.playerprofile.model.PlayerProfileDTO;

import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class MatchNotificationDTOTest {

    @Test
    void testNoArgsConstructor() {
        MatchNotificationDTO matchNotificationDTO = new MatchNotificationDTO();
        assertNotNull(matchNotificationDTO);
    }

    @Test
    void testAllArgsConstructor() {
        UUID matchId = UUID.randomUUID();
        double meetingLatitude = 40.7128;
        double meetingLongitude = -74.0060;
        String opponentName = "John Doe";
        PlayerProfileDTO opponentProfile = new PlayerProfileDTO();

        MatchNotificationDTO matchNotificationDTO = new MatchNotificationDTO(matchId, meetingLatitude, meetingLongitude,
                opponentName, opponentProfile);

        assertEquals(matchId, matchNotificationDTO.getMatchId());
        assertEquals(meetingLatitude, matchNotificationDTO.getMeetingLatitude());
        assertEquals(meetingLongitude, matchNotificationDTO.getMeetingLongitude());
        assertEquals(opponentName, matchNotificationDTO.getOpponentName());
        assertEquals(opponentProfile, matchNotificationDTO.getOpponentProfile());
    }

    @Test
    void testSettersAndGetters() {
        MatchNotificationDTO matchNotificationDTO = new MatchNotificationDTO();

        UUID matchId = UUID.randomUUID();
        double meetingLatitude = 40.7128;
        double meetingLongitude = -74.0060;
        String opponentName = "John Doe";
        PlayerProfileDTO opponentProfile = new PlayerProfileDTO();

        matchNotificationDTO.setMatchId(matchId);
        matchNotificationDTO.setMeetingLatitude(meetingLatitude);
        matchNotificationDTO.setMeetingLongitude(meetingLongitude);
        matchNotificationDTO.setOpponentName(opponentName);
        matchNotificationDTO.setOpponentProfile(opponentProfile);

        assertEquals(matchId, matchNotificationDTO.getMatchId());
        assertEquals(meetingLatitude, matchNotificationDTO.getMeetingLatitude());
        assertEquals(meetingLongitude, matchNotificationDTO.getMeetingLongitude());
        assertEquals(opponentName, matchNotificationDTO.getOpponentName());
        assertEquals(opponentProfile, matchNotificationDTO.getOpponentProfile());
    }
}
