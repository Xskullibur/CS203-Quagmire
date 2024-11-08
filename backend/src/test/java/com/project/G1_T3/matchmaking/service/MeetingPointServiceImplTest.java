package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.matchmaking.model.QueuedPlayer;
import com.project.G1_T3.player.model.PlayerProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class MeetingPointServiceImplTest {

    private MeetingPointService meetingPointService;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        meetingPointService = new MeetingPointServiceImpl(restTemplate);
    }

    @Test
    void findMeetingPoint_shouldReturnMidpoint() {
        PlayerProfile mockProfile1 = mock(PlayerProfile.class);
        PlayerProfile mockProfile2 = mock(PlayerProfile.class);

        QueuedPlayer player1 = new QueuedPlayer(mockProfile1, 0, 0);
        QueuedPlayer player2 = new QueuedPlayer(mockProfile2, 10, 10);

        double[] meetingPoint = meetingPointService.findMeetingPoint(player1, player2);

        assertEquals(5, meetingPoint[0], 0.001);
        assertEquals(5, meetingPoint[1], 0.001);
    }

    @Test
    void findMeetingPoint_sameLocation_shouldReturnSamePoint() {
        PlayerProfile mockProfile1 = mock(PlayerProfile.class);
        PlayerProfile mockProfile2 = mock(PlayerProfile.class);

        QueuedPlayer player1 = new QueuedPlayer(mockProfile1, 5, 5);
        QueuedPlayer player2 = new QueuedPlayer(mockProfile2, 5, 5);

        double[] meetingPoint = meetingPointService.findMeetingPoint(player1, player2);

        assertEquals(5, meetingPoint[0], 0.001);
        assertEquals(5, meetingPoint[1], 0.001);
    }

    @Test
    void findMeetingPoint_negativeCoordinates_shouldWork() {
        PlayerProfile mockProfile1 = mock(PlayerProfile.class);
        PlayerProfile mockProfile2 = mock(PlayerProfile.class);

        QueuedPlayer player1 = new QueuedPlayer(mockProfile1, -10, -10);
        QueuedPlayer player2 = new QueuedPlayer(mockProfile2, 10, 10);

        double[] meetingPoint = meetingPointService.findMeetingPoint(player1, player2);

        assertEquals(0, meetingPoint[0], 0.001);
        assertEquals(0, meetingPoint[1], 0.001);
    }

    // Add more tests here if we implement the API call functionality
}