package com.project.G1_T3.achievement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.G1_T3.playerprofile.model.PlayerProfile;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "achievements")
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "criteria_type", nullable = false)
    private String criteriaType; // e.g., "participation", "rating"

    @Column(name = "criteria_count", nullable = false)
    private int criteriaCount; // Numeric criteria for achievement

    @ManyToMany(mappedBy = "achievements")
    @JsonIgnore
    private Set<PlayerProfile> players;
}
