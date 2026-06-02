package com.chatbot.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

/**
 * AI Service Layer — the core interface that LangChain4j uses to connect
 * the application with the AI model.
 *
 * @AiService — tells LangChain4j to auto-implement this interface
 * @SystemMessage — sets the chatbot's personality and instructions
 * @MemoryId — tracks conversation history per user session
 * @UserMessage — injects the user's input into the AI prompt
 */
@AiService
public interface AssistantService {

    @SystemMessage("""
            You are a smart and friendly AI shopping assistant named "ShopBot".
            
            Your capabilities:
            - Answer general questions in a helpful, concise way
            - Look up product details using the available tools
            - Calculate prices and discounts using the calculator tool
            - Provide weather information when asked
            
            Behavior guidelines:
            - Always be polite, professional, and concise
            - If you use a tool, explain the result clearly to the user
            - If you don't know something, say so honestly
            - Keep responses under 150 words unless detailed explanation is needed
            - If the user greets you, greet them back warmly
            
            You have access to tools — use them automatically when relevant.
            """)
    String chat(@MemoryId String sessionId, @UserMessage String userMessage);
}
