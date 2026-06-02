package com.chatbot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response body returned by the /chat endpoint.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String sessionId;   // Echo back the session ID
    private String reply;       // The AI's response
    private boolean success;    // Whether the request succeeded
    private String error;       // Error message if success = false

    // ── Static factory helpers ────────────────────────────────────────────────

    public static ChatResponse ok(String sessionId, String reply) {
        return new ChatResponse(sessionId, reply, true, null);
    }

    public static ChatResponse error(String sessionId, String errorMessage) {
        return new ChatResponse(sessionId, null, false, errorMessage);
    }
}
