package com.project.G1_T3.common.glicko;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Glicko2Rating {
    private static final double TAU = 0.5; // System constant, usually between 0.3 and 1.2
    private static final double EPSILON = 0.000001; // Convergence tolerance

    // Conversion constants
    private static final double SCALE = 173.7178;
    private static final double INITIAL_RATING = 1500;
    private static final double INITIAL_RD = 350;
    private static final double INITIAL_VOLATILITY = 0.06;

    private double rating; // R: actual rating of the player
    private double ratingDeviation; // RD: increases when the player's skills level are unknown, eg, when they haven't played in a long time
    private double volatility; // sigma: volatility tracks the consitency of the player's perfomrance

    public Glicko2Rating() {
        this.rating = INITIAL_RATING;
        this.ratingDeviation = INITIAL_RD;
        this.volatility = INITIAL_VOLATILITY;
    }

    public Glicko2Rating(double rating, double ratingDeviation, double volatility) {
        this.rating = rating;
        this.ratingDeviation = ratingDeviation;
        this.volatility = volatility;
    }

    public void updateRating(List<Glicko2Result> results) {
        if (results.isEmpty()) {
            // No games played; increase the RD to reflect uncertainty over time
            double preRatingRD = Math.sqrt(ratingDeviation * ratingDeviation + volatility * volatility);
            ratingDeviation = preRatingRD;
            return;
        }

        // Step 1: Convert the ratings to the Glicko-2 scale
        double mu = (rating - INITIAL_RATING) / SCALE;
        double phi = ratingDeviation / SCALE;
        double sigma = volatility;

        // Opponent ratings and deviations in Glicko-2 scale
        double[] mu_j = new double[results.size()];
        double[] phi_j = new double[results.size()];
        double[] s_j = new double[results.size()];

        for (int i = 0; i < results.size(); i++) {
            Glicko2Result result = results.get(i);
            mu_j[i] = (result.getOpponentRating() - INITIAL_RATING) / SCALE;
            phi_j[i] = result.getOpponentRatingDeviation() / SCALE;
            s_j[i] = result.getScore(); // 1.0 for win, 0.0 for loss, 0.5 for draw
        }

        // Step 2: Compute the estimated variance (v)
        double v = 0;
        for (int i = 0; i < results.size(); i++) {
            double gPhi = g(phi_j[i]);
            double e = E(mu, mu_j[i], phi_j[i]);
            v += gPhi * gPhi * e * (1 - e);
        }
        v = 1.0 / v;

        // Step 3: Compute the difference between the expected and actual score (delta)
        double delta = 0;
        for (int i = 0; i < results.size(); i++) {
            double gPhi = g(phi_j[i]);
            double e = E(mu, mu_j[i], phi_j[i]);
            delta += gPhi * (s_j[i] - e);
        }
        delta = v * delta;

        // Step 4: Update volatility
        double a = Math.log(sigma * sigma);
        double A = a;
        double B;
        if (delta * delta > phi * phi + v) {
            B = Math.log(delta * delta - phi * phi - v);
        } else {
            double k = 1;
            while (f(a - k * TAU, delta, phi, v, a) < 0) {
                k++;
            }
            B = a - k * TAU;
        }

        // Find the root using the Newton-Raphson method
        double fA = f(A, delta, phi, v, a);
        double fB = f(B, delta, phi, v, a);

        while (Math.abs(B - A) > EPSILON) {
            double C = A + (A - B) * fA / (fB - fA);
            double fC = f(C, delta, phi, v, a);
            if (fC * fB < 0) {
                A = B;
                fA = fB;
            } else {
                fA /= 2.0;
            }
            B = C;
            fB = fC;
        }
        double sigmaPrime = Math.exp(A / 2.0);

        // Step 5: Update the rating deviation (phi*)
        double phiStar = Math.sqrt(phi * phi + sigmaPrime * sigmaPrime);

        // Step 6: Update the rating (mu')
        double phiPrime = 1.0 / Math.sqrt(1.0 / (phiStar * phiStar) + 1.0 / v);
        double muPrime = mu + phiPrime * phiPrime * delta / v;

        // Convert back to original scale
        rating = Math.max(Math.min(muPrime * SCALE + INITIAL_RATING, 3000), 1);
        ratingDeviation = phiPrime * SCALE;
        volatility = sigmaPrime;

        // Ensure RD doesn't drop below a minimum value (e.g., 30)
        ratingDeviation = Math.max(ratingDeviation, 30);
    }

    // Helper functions
    private double g(double phi) {
        return 1.0 / Math.sqrt(1.0 + 3.0 * phi * phi / (Math.PI * Math.PI));
    }

    private double E(double mu, double mu_j, double phi_j) {
        return 1.0 / (1.0 + Math.exp(-g(phi_j) * (mu - mu_j)));
    }

    private double f(double x, double delta, double phi, double v, double a) {
        double eX = Math.exp(x);
        double numerator = eX * (delta * delta - phi * phi - v - eX);
        double denominator = 2.0 * (phi * phi + v + eX) * (phi * phi + v + eX);
        return (numerator / denominator) - ((x - a) / (TAU * TAU));
    }
}
