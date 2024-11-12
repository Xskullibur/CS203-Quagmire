package com.project.G1_T3.common.exception.tournament;

public class InsufficientPlayersException extends RuntimeException {
    public InsufficientPlayersException(String message) {
        super(message);
    }
}