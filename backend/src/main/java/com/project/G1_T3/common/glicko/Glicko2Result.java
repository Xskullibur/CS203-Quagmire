package com.project.G1_T3.common.glicko;

public class Glicko2Result {
    private double opponentRating;
    private double opponentRatingDeviation;
    private double score; // 1.0 for win, 0.5 for draw, 0.0 for loss

    public Glicko2Result(double opponentRating, double opponentRatingDeviation, double score) {
        this.opponentRating = opponentRating;
        this.opponentRatingDeviation = opponentRatingDeviation;
        this.score = score;
    }

    // Getters
    public double getOpponentRating() {
        return opponentRating;
    }

    public double getOpponentRatingDeviation() {
        return opponentRatingDeviation;
    }

    public double getScore() {
        return score;
    }
}
