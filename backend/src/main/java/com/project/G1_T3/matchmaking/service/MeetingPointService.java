package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.matchmaking.model.QueuedPlayer;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MeetingPointService {

    private final RestTemplate restTemplate;

    public MeetingPointService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double[] findMeetingPoint(QueuedPlayer player1, QueuedPlayer player2) {
        // This is a placeholder implementation. In a real-world scenario,
        // you would integrate with a service like Google Places API or OpenStreetMap
        // to find a suitable meeting point (e.g., a public place) between the two
        // players.

        // For now, we'll just return the midpoint, but in reality, you'd make an API
        // call here
        double midLat = (player1.getLatitude() + player2.getLatitude()) / 2;
        double midLon = (player1.getLongitude() + player2.getLongitude()) / 2;

        // Simulating an API call to find a nearby public place
        // String url = "https://api.example.com/places?lat=" + midLat + "&lon=" +
        // midLon;
        // PlaceResponse response = restTemplate.getForObject(url, PlaceResponse.class);
        // return new double[] { response.getLatitude(), response.getLongitude() };

        // For now, just return the midpoint
        return new double[] { midLat, midLon };
    }
}