package com.project.G1_T3.player.service;

import com.project.G1_T3.player.repository.PlayerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import com.project.G1_T3.player.model.PlayerProfile;


public class PlayerProfileService {
    
    @Autowired
    private PlayerProfileRepository playerProfileRepository;

    public List<PlayerProfile> findAll() {
        return playerProfileRepository.findAll();
    }

    public PlayerProfile save(PlayerProfile profile){
        return playerProfileRepository.save(profile);
    }


}
