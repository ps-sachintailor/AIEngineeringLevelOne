package com.productsquads.aiengineering.ask;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

class QuestionAnswerServiceTests {

    private final ChatClient chatClient = mock(ChatClient.class, RETURNS_DEEP_STUBS);
    private final QuestionAnswerService service = new QuestionAnswerService(chatClient);

    @Test
    void returnsGeneratedModelContent() {
        when(chatClient.prompt().user("What is RAG?").call().content())
                .thenReturn("A grounded generation technique.");

        assertThat(service.answer("What is RAG?"))
                .isEqualTo("A grounded generation technique.");
    }

    @Test
    void convertsProviderFailureToControlledApplicationException() {
        when(chatClient.prompt()).thenThrow(new IllegalStateException("provider secret"));

        assertThatThrownBy(() -> service.answer("What is RAG?"))
                .isInstanceOf(ModelRequestException.class)
                .hasMessage("The AI model request failed")
                .hasCauseInstanceOf(IllegalStateException.class);
    }

    @Test
    void rejectsBlankModelContent() {
        when(chatClient.prompt().user("What is RAG?").call().content()).thenReturn("   ");

        assertThatThrownBy(() -> service.answer("What is RAG?"))
                .isInstanceOf(ModelRequestException.class)
                .hasMessage("The AI model did not return an answer");
    }
}
