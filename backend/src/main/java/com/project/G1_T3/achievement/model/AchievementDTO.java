package com.project.G1_T3.achievement.model;

public class AchievementDTO {
    private Long id;
    private String name;
    private String description;

    public AchievementDTO(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
