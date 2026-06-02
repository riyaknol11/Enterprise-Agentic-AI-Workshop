package com.chatbot.exception;

/**
 * Thrown when user provides invalid or empty input.
 */
public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}
