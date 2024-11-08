package com.project.G1_T3.matchmaking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class LocationServiceImplTest {

    private LocationService locationService;

    @BeforeEach
    void setUp() {
        locationService = new LocationServiceImpl();
    }

    @Test
    void calculateDistance_samePoint_shouldReturnZero() {
        double distance = locationService.calculateDistance(0, 0, 0, 0);
        assertEquals(0, distance, 0.001);
    }

    @Test
    void calculateDistance_knownDistance_shouldReturnCorrectValue() {
        // New York to Los Angeles, approximately 3936 km
        double distance = locationService.calculateDistance(40.7128, -74.0060, 34.0522, -118.2437);
        assertEquals(3936, distance, 1);
    }

    @Test
    void calculateDistance_antipodes_shouldReturnHalfEarthCircumference() {
        // Antipodes should be about 20,015 km apart (half Earth's circumference)
        double distance = locationService.calculateDistance(0, 0, 0, 180);
        assertEquals(20015, distance, 1);
    }

    @Test
    void calculateDistance_negativeCoordinates_shouldWork() {
        double distance = locationService.calculateDistance(-33.8688, 151.2093, -23.5505, -46.6333);
        assertTrue(distance > 0);
    }

    @Test
    void calculateDistance_extremeLatitudes_shouldWork() {
        double distance = locationService.calculateDistance(90, 0, -90, 0);
        assertEquals(20015, distance, 1);
    }
}