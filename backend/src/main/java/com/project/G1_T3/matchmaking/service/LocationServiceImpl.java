package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.common.exception.LocationServiceException;
import org.springframework.stereotype.Service;

/**
 * Service implementation for location-based operations.
 */
@Service
public class LocationServiceImpl implements LocationService {
    private static final int EARTH_RADIUS_KM = 6371; // Radius of the Earth in kilometers

    /**
     * Calculates the distance between two points specified by their latitude and longitude
     * using the Haversine formula.
     *
     * @param lat1 Latitude of the first point
     * @param lon1 Longitude of the first point
     * @param lat2 Latitude of the second point
     * @param lon2 Longitude of the second point
     * @return The distance between the two points in kilometers
     * @throws LocationServiceException if an error occurs during the calculation
     */
    @Override
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        try {
            // Convert latitude and longitude from degrees to radians
            double dLat = Math.toRadians(lat2 - lat1);
            double dLon = Math.toRadians(lon2 - lon1);
            lat1 = Math.toRadians(lat1);
            lat2 = Math.toRadians(lat2);

            // Haversine formula to calculate the great-circle distance between two points
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(lat1) * Math.cos(lat2) *
                            Math.sin(dLon / 2) * Math.sin(dLon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            // Return the distance in kilometers
            return EARTH_RADIUS_KM * c;
        } catch (Exception e) {
            // Throw a custom exception if an error occurs
            throw new LocationServiceException("Error calculating distance: " + e.getMessage(), e);
        }
    }
}