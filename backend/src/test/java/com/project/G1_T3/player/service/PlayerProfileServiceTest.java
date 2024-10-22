package com.project.G1_T3.player.service;

import com.project.G1_T3.player.model.PlayerProfile;
import com.project.G1_T3.player.repository.PlayerProfileRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
public class PlayerProfileServiceTest {

    @Mock
    private PlayerProfileRepository playerProfileRepository;

    @InjectMocks
    private PlayerRatingService playerRatingService;

    @InjectMocks
    private PlayerProfileService playerProfileService;


    @BeforeEach
    public void setUp() {
        // Mock the getRatingCounts() method
        MockitoAnnotations.openMocks(this);
        List<Object[]> mockCounts = new ArrayList<>();
        mockCounts.add(new Object[] { 1500, 10L });
        mockCounts.add(new Object[] { 1501, 5L });
        mockCounts.add(new Object[] { 1499, 8L });

        when(playerProfileRepository.getRatingCounts()).thenReturn(mockCounts);

        // Initialize the buckets
        playerRatingService.initializeBuckets();
    }


}
