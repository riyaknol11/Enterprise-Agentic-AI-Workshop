package com.chatbot.config;

import com.chatbot.service.AssistantService;
import com.chatbot.tools.ChatTools;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LangChain4j Configuration
 *
 * This class manually wires together:
 *  - The AI model (OpenAI GPT) with specific parameters
 *  - The chat memory (per-session conversation history)
 *  - The custom tools
 *  - The AssistantService interface

 * Model Parameters Explained:
 *  temperature  → 0.0 = very predictable/factual, 1.0 = very creative/random
 *  maxTokens    → limits how long the AI's response can be
 */
@Configuration
public class LangChain4jConfig {

    @Value("${langchain4j.open-ai.chat-model.api-key}")
    private String openAiApiKey;

    @Value("${langchain4j.open-ai.chat-model.model-name:gpt-3.5-turbo}")
    private String modelName;

    @Value("${langchain4j.open-ai.chat-model.temperature:0.7}")
    private double temperature;

    @Value("${langchain4j.open-ai.chat-model.max-tokens:500}")
    private int maxTokens;

    /**
     * Configures the OpenAI model with specific parameters.
     * Adjust temperature and max-tokens in application.properties to see
     * how the chatbot behavior changes.
     */
    @Bean
    public OpenAiChatModel openAiChatModel() {
        return OpenAiChatModel.builder()
                .apiKey(openAiApiKey)
                .modelName(modelName)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .logRequests(true)   // Logs requests for debugging
                .logResponses(true)  // Logs responses for debugging
                .build();
    }

    /**
     * Builds the AssistantService with:
     * - OpenAI model (with configured parameters)
     * - Chat memory per session (stores last 10 messages)
     * - Custom tools (product lookup, calculator, date/time)
     */
    @Bean
    public AssistantService assistantService(OpenAiChatModel chatModel, ChatTools chatTools) {
        return AiServices.builder(AssistantService.class)
                .chatLanguageModel(chatModel)
                .chatMemoryProvider(sessionId ->
                        MessageWindowChatMemory.withMaxMessages(10)
                )
                .tools(chatTools)
                .build();
    }
}
