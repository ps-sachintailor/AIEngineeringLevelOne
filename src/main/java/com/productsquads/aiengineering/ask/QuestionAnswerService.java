package com.productsquads.aiengineering.ask;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class QuestionAnswerService {

    private final ChatClient chatClient;

    @Autowired
    public QuestionAnswerService(ChatClient.Builder chatClientBuilder) {
        this(chatClientBuilder.build());
    }

    QuestionAnswerService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String answer(String question) {
        final String answer;
        try {
            answer = chatClient.prompt()
                    .user(question)
                    .call()
                    .content();
        } catch (RuntimeException exception) {
            throw new ModelRequestException(exception);
        }

        if (!StringUtils.hasText(answer)) {
            throw new ModelRequestException();
        }
        return answer;
    }
}
