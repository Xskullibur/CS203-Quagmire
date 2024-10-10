package com.project.G1_T3.matchmaking.service;

import java.util.UUID;
import com.project.G1_T3.match.model.Match;

public interface MatchService {
    Match getCurrentMatchForUser(UUID userId);
}