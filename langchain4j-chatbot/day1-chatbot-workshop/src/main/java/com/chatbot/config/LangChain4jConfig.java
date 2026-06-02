package com.chatbot.config;

import com.chatbot.service.AssistantService;
import com.chatbot.tools.ChatTools;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChain4jConfig {

    @Value("${langchain4j.open-ai.chat-model.api-key}")
    private String openAiApiKey;

    @Bean
    public OpenAiChatModel openAiChatModel() {
        return OpenAiChatModel.builder()
                .apiKey(openAiApiKey)
                .modelName("gpt-3.5-turbo")
                .temperature(0.7)
                .maxTokens(500)
                .build();
    }

    @Bean
    public AssistantService assistantService(OpenAiChatModel chatModel, ChatTools chatTools) {
        return AiServices.builder(AssistantService.class)
                .chatLanguageModel(chatModel)
                .chatMemoryProvider(sessionId ->
                        MessageWindowChatMemory.withMaxMessages(10))
                .tools(chatTools)
                .build();
    }
}