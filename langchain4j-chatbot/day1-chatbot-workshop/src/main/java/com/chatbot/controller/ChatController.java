package com.chatbot.controller;

import com.chatbot.exception.InvalidInputException;
import com.chatbot.model.ChatRequest;
import com.chatbot.model.ChatResponse;
import com.chatbot.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ChatController — exposes REST endpoints for the chatbot.
 *
 * Endpoints:
 *  POST /api/chat          → Send a message, get an AI response
 *  GET  /api/chat/health   → Check if the chatbot is running
 *  POST /api/chat/simulate-error → Simulate an AI failure (for demo purposes)
 */
@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // ─── POST /api/chat ───────────────────────────────────────────────────────

    /**
     * Main chat endpoint. Accepts a message and returns the AI's reply.
     *
     * Example body:
     * {
     *   "sessionId": "user-123",
     *   "message": "Tell me about laptops"
     * }
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        log.info("Received chat request: {}", request);

        try {
            ChatResponse response = chatService.processChat(request);

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                // AI failed — return 503 Service Unavailable with fallback message
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
            }

        } catch (InvalidInputException e) {
            // Bad user input — return 400 Bad Request
            log.warn("Invalid input: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ChatResponse.error(
                            request != null ? request.getSessionId() : "unknown",
                            "Invalid input: " + e.getMessage()
                    ));
        } catch (Exception e) {
            // Unexpected server error — return 500
            log.error("Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ChatResponse.error("unknown",
                            "Unexpected server error. Please try again."));
        }
    }

    // ─── GET /api/chat/health ─────────────────────────────────────────────────

    /**
     * Health check endpoint — verify the service is running.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "LangChain4j AI Chatbot",
                "message", "ShopBot is ready to chat!"
        ));
    }

    // ─── POST /api/chat/simulate-error ───────────────────────────────────────

    /**
     * Simulates an AI failure scenario to demonstrate error handling.
     * Useful for testing and screenshots of fallback behavior.
     */
    @PostMapping("/simulate-error")
    public ResponseEntity<ChatResponse> simulateError() {
        log.warn("Simulating AI failure scenario...");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ChatResponse.error("test-session",
                        "AI Error: Connection timeout (simulated) | " +
                        "Fallback: I'm sorry, I'm having trouble connecting to my AI brain right now. " +
                        "Please try again in a moment."));
    }

    // ─── POST /api/chat/empty-input-error ────────────────────────────────────

    /**
     * Demonstrates invalid input handling.
     */
    @PostMapping("/empty-input-error")
    public ResponseEntity<ChatResponse> emptyInputError() {
        ChatRequest badRequest = new ChatRequest("demo-session", "");
        try {
            chatService.processChat(badRequest);
            return ResponseEntity.ok(ChatResponse.ok("demo-session", "No error (unexpected)"));
        } catch (InvalidInputException e) {
            return ResponseEntity.badRequest()
                    .body(ChatResponse.error("demo-session", "Invalid input: " + e.getMessage()));
        }
    }
}
