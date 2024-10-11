package com.project.G1_T3.matchmaking.service;

import com.project.G1_T3.matchmaking.model.QueuedPlayer;
import com.project.G1_T3.common.exception.MeetingPointNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MeetingPointServiceImpl implements MeetingPointService {

    private final RestTemplate restTemplate;

    public MeetingPointServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
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
        // try {
        // PlaceResponse response = restTemplate.getForObject(url, PlaceResponse.class);
        // if (response != null) {
        // return new double[] { response.getLatitude(), response.getLongitude() };
        // } else {
        // throw new MeetingPointNotFoundException("No suitable meeting point found");
        // }
        // } catch (Exception e) {
        // throw new MeetingPointNotFoundException("Error finding meeting point: " +
        // e.getMessage());
        // }

        // For now, just return the midpoint
        // In a real implementation, you might want to add some checks here
        // to ensure the midpoint is valid (e.g., not in the middle of a lake)
        if (isValidMeetingPoint(midLat, midLon)) {
            return new double[] { midLat, midLon };
        } else {
            throw new MeetingPointNotFoundException("No suitable meeting point found");
        }
    }

    private boolean isValidMeetingPoint(double latitude, double longitude) {
        // Implement logic to check if the point is valid
        // This could involve checking against a database of valid locations,
        // or making an API call to a mapping service
        // For now, we'll just return true
        return true;
    }
}