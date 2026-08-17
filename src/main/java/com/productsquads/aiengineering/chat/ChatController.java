package com.productsquads.aiengineering.chat;

import com.productsquads.aiengineering.config.AiModelProperties;
import jakarta.validation.Valid;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/chat", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatController {

    private final ChatClient chatClient;
    private final AiModelProperties properties;

    public ChatController(ChatClient.Builder chatClientBuilder, AiModelProperties properties) {
        this.chatClient = chatClientBuilder.build();
        this.properties = properties;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return generate(request.message());
    }

    @GetMapping
    ChatResponse chatInBrowser(
            @RequestParam(defaultValue = "Reply with a short greeting.") String message) {
        return generate(message);
    }

    private ChatResponse generate(String message) {
        String answer = chatClient.prompt()
                .user(message)
                .call()
                .content();
        return new ChatResponse(properties.provider(), properties.model(), answer);
    }
}
