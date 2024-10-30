package com.project.G1_T3.email.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EmailResponse {
    private String message;
    private boolean success;
}