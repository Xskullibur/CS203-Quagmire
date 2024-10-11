package com.project.G1_T3.common.exception;

public class MatchmakingException extends RuntimeException {
    public MatchmakingException(String message) {
        super(message);
    }

    public MatchmakingException(String message, Throwable cause) {
        super(message, cause);
    }
}
