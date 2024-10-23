package com.project.G1_T3.authentication.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.G1_T3.validation.NoSpaces;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterRequest {

    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @NoSpaces(message = "Username cannot contain spaces")
    private String username;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 7, message = "Password must be at least 7 characters")
    private String password;

    public RegisterRequest() {
        super();
    }

    public RegisterRequest(
            @NotBlank(message = "Username is mandatory") @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters") String username,
            @NotBlank(message = "Email is mandatory") @Email(message = "Email should be valid") String email,
            @NotBlank(message = "Password is mandatory") @Size(min = 7, message = "Password must be at least 7 characters") String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}