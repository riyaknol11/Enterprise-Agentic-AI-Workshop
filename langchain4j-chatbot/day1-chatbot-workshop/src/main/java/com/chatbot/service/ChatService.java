package com.chatbot.service;

import com.chatbot.exception.InvalidInputException;
import com.chatbot.model.ChatRequest;
import com.chatbot.model.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * ChatService — orchestrates the chatbot flow:
 *  1. Validates input
 *  2. Calls AssistantService (which talks to the AI model)
 *  3. Returns a structured response
 *  4. Handles errors gracefully with fallback messages
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final AssistantService assistantService;

    // Fallback message shown when the AI model fails
    private static final String FALLBACK_MESSAGE =
            "I'm sorry, I'm having trouble connecting to my AI brain right now. " +
            "Please try again in a moment. If the issue persists, check your API key configuration.";

    /**
     * Processes a chat request end-to-end.
     *
     * @param request the incoming chat request
     * @return ChatResponse with either the AI reply or an error
     */
    public ChatResponse processChat(ChatRequest request) {

        // ── Step 1: Input Validation ──────────────────────────────────────────
        validateRequest(request);

        // ── Step 2: Assign session ID if missing ──────────────────────────────
        String sessionId = (request.getSessionId() == null || request.getSessionId().isBlank())
                ? UUID.randomUUID().toString()
                : request.getSessionId();

        log.info("Processing chat | session={} | message={}", sessionId, request.getMessage());

        // ── Step 3: Call the AI model ─────────────────────────────────────────
        try {
            String reply = assistantService.chat(sessionId, request.getMessage());
            log.info("AI replied | session={} | reply length={}", sessionId, reply.length());
            return ChatResponse.ok(sessionId, reply);

        } catch (Exception e) {
            // ── Step 4: Fallback on AI failure ────────────────────────────────
            log.error("AI model failed | session={} | error={}", sessionId, e.getMessage(), e);
            return ChatResponse.error(sessionId,
                    "AI Error: " + e.getMessage() + " | Fallback: " + FALLBACK_MESSAGE);
        }
    }

    /**
     * Validates the incoming request.
     * Throws InvalidInputException for bad input so the controller can handle it.
     */
    private void validateRequest(ChatRequest request) {
        if (request == null) {
            throw new InvalidInputException("Request body cannot be null.");
        }
        if (request.getMessage() == null || request.getMessage().isBlank()) {
            throw new InvalidInputException("Message cannot be empty. Please type something!");
        }
        if (request.getMessage().length() > 1000) {
            throw new InvalidInputException("Message too long. Please keep it under 1000 characters.");
        }
    }
}
