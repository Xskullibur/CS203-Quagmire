package com.project.G1_T3.player.service;

import com.project.G1_T3.player.repository.PlayerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import com.project.G1_T3.player.model.PlayerProfile;

@Service
public class PlayerProfileService {
    
    
    @Autowired
    private PlayerProfileRepository playerProfileRepository;

    public List<PlayerProfile> findAll() {
        return playerProfileRepository.findAll();
    }

    public PlayerProfile findByUserId(String id){
        return playerProfileRepository.findByUserId(UUID.fromString(id));
    }

    public PlayerProfile save(PlayerProfile profile){
        return playerProfileRepository.save(profile);
    }

}
