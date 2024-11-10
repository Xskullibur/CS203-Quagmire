package com.project.G1_T3.common.glicko;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

class Glicko2RatingTest {

    @Test
    void testRatingIncreaseAfterWin() {
        // Arrange
        Glicko2Rating player = new Glicko2Rating(1500, 200, 0.06);
        Glicko2Result result = new Glicko2Result(1400, 30, 1.0); // Player wins against lower-rated opponent

        // Act
        player.updateRating(Arrays.asList(result));

        // Assert
        assertTrue(player.getRating() > 1500, "Rating should increase after a win");
    }

    @Test
    void testRatingDecreaseAfterLoss() {
        // Arrange
        Glicko2Rating player = new Glicko2Rating(1500, 200, 0.06);
        Glicko2Result result = new Glicko2Result(1600, 30, 0.0); // Player loses against higher-rated opponent

        // Act
        player.updateRating(Arrays.asList(result));

        // Assert
        assertTrue(player.getRating() < 1500, "Rating should decrease after a loss");
    }

    @Test
    void testRatingUnchangedAfterDraw() {
        // Arrange
        Glicko2Rating player = new Glicko2Rating(1500, 200, 0.06);
        Glicko2Result result = new Glicko2Result(1500, 200, 0.5); // Draw against equally rated opponent

        // Act
        player.updateRating(Arrays.asList(result));

        // Assert
        assertEquals(1500, player.getRating(), 0.01, "Rating should remain approximately the same after a draw");
    }

    @Test
    void testRatingDeviationDecrease() {
        // Arrange
        Glicko2Rating player = new Glicko2Rating(1500, 200, 0.06);
        Glicko2Result result = new Glicko2Result(1500, 200, 0.5);

        // Act
        player.updateRating(Arrays.asList(result));

        // Assert
        assertTrue(player.getRatingDeviation() < 200, "Rating deviation should decrease after a match");
    }

    @Test
    void testVolatilityWithinBounds() {
        // Arrange
        Glicko2Rating player = new Glicko2Rating(1500, 200, 0.06);
        Glicko2Result result = new Glicko2Result(1500, 200, 0.5);

        // Act
        player.updateRating(Arrays.asList(result));

        // Assert
        assertTrue(player.getVolatility() > 0, "Volatility should remain positive");
    }
}
