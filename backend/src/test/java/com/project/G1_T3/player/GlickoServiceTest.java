package com.project.G1_T3.player;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.project.G1_T3.player.model.*;
import com.project.G1_T3.player.service.*;

public class GlickoServiceTest {

    private GlickoService glickoService;

    @BeforeEach
    public void setUp() {
        glickoService = new GlickoService();
    }

    @Test
    public void testRatingUpdateForPlayer1Win() {
        // Initialize player profiles with example values
        PlayerProfile player1 = new PlayerProfile();
        player1.setGlickoRating(1500.0f); // Initial rating
        player1.setRatingDeviation(350.0f); // Initial RD
        player1.setVolatility(0.06f); // Initial volatility

        PlayerProfile player2 = new PlayerProfile();
        player2.setGlickoRating(1400.0f); // Initial rating
        player2.setRatingDeviation(300.0f); // Initial RD
        player2.setVolatility(0.05f); // Initial volatility

        // Store initial values for assertions
        float initialRating1 = player1.getGlickoRating();
        float initialRating2 = player2.getGlickoRating();
        float initialRD1 = player1.getRatingDeviation();
        float initialRD2 = player2.getRatingDeviation();

        // Simulate player1 winning
        glickoService.updateRatings(player1, player2, true);

        // Assertions to verify expected changes in ratings
        assertNotEquals(initialRating1, player1.getGlickoRating(), "Player 1's rating should have changed");
        assertNotEquals(initialRating2, player2.getGlickoRating(), "Player 2's rating should have changed");

        // Verify if the ratings are updated in the expected direction
        assertTrue(player1.getGlickoRating() > initialRating1, "Player 1's rating should increase");
        assertTrue(player2.getGlickoRating() < initialRating2, "Player 2's rating should decrease");

        // Assertions to verify expected changes in RD
        assertNotEquals(initialRD1, player1.getRatingDeviation(), "Player 1's RD should have changed");
        assertNotEquals(initialRD2, player2.getRatingDeviation(), "Player 2's RD should have changed");

        // Verify the direction of the RD change if applicable
        assertTrue(player1.getRatingDeviation() > initialRD1, "Player 1's RD should increase after the match");
        assertTrue(player2.getRatingDeviation() > initialRD2, "Player 2's RD should increase after the match");
    }

    @Test
    public void testRatingUpdateForPlayer2Win() {
        // Similar setup for testing player 2 winning
        PlayerProfile player1 = new PlayerProfile();
        player1.setGlickoRating(1500.0f);
        player1.setRatingDeviation(350.0f);
        player1.setVolatility(0.06f);

        PlayerProfile player2 = new PlayerProfile();
        player2.setGlickoRating(1400.0f);
        player2.setRatingDeviation(300.0f);
        player2.setVolatility(0.05f);

        // Store initial values for assertions
        float initialRating1 = player1.getGlickoRating();
        float initialRating2 = player2.getGlickoRating();
        float initialRD1 = player1.getRatingDeviation();
        float initialRD2 = player2.getRatingDeviation();

        // Simulate player2 winning
        glickoService.updateRatings(player1, player2, false);

        // Assertions to verify expected changes in ratings
        assertNotEquals(initialRating1, player1.getGlickoRating(), "Player 1's rating should have changed");
        assertNotEquals(initialRating2, player2.getGlickoRating(), "Player 2's rating should have changed");

        // Verify if the ratings are updated in the expected direction
        assertTrue(player1.getGlickoRating() < initialRating1, "Player 1's rating should decrease");
        assertTrue(player2.getGlickoRating() > initialRating2, "Player 2's rating should increase");

        // Assertions to verify expected changes in RD
        assertNotEquals(initialRD1, player1.getRatingDeviation(), "Player 1's RD should have changed");
        assertNotEquals(initialRD2, player2.getRatingDeviation(), "Player 2's RD should have changed");

        // Verify the direction of the RD change if applicable
        assertTrue(player1.getRatingDeviation() > initialRD1, "Player 1's RD should increase after the match");
        assertTrue(player2.getRatingDeviation() > initialRD2, "Player 2's RD should increase after the match");
    }
}
