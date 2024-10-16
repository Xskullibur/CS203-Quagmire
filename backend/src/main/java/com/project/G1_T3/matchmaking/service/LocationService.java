package com.project.G1_T3.matchmaking.service;

public interface LocationService {
    double calculateDistance(double lat1, double lon1, double lat2, double lon2);
}