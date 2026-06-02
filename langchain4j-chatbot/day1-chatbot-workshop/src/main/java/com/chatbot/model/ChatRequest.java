package com.chatbot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for the /chat endpoint.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    private String sessionId;   // To identify the user session (for memory)
    private String message;     // The user's message
}
