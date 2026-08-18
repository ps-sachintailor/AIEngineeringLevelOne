package com.productsquads.aiengineering.ask;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

class QuestionAnswerServiceTests {

    private final ChatClient chatClient = mock(ChatClient.class, RETURNS_DEEP_STUBS);
    private final VectorStore vectorStore = mock(VectorStore.class);
    private final QuestionAnswerService service = new QuestionAnswerService(chatClient, vectorStore);

    @Test
    void searchesDocumentsBeforeGeneratingGroundedModelContent() {
        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(List.of(new Document(
                        "RAG retrieves relevant documents before generating an answer.")));
        ChatClient.ChatClientRequestSpec promptSpec = chatClient.prompt();
        when(promptSpec.user(anyString()).call().content())
                .thenReturn("A grounded generation technique.");
        clearInvocations(chatClient, promptSpec);

        assertThat(service.answer("What is RAG?"))
                .isEqualTo("A grounded generation technique.");

        InOrder inOrder = inOrder(vectorStore, chatClient);
        inOrder.verify(vectorStore).similaritySearch(any(SearchRequest.class));
        inOrder.verify(chatClient).prompt();

        ArgumentCaptor<SearchRequest> searchRequest = ArgumentCaptor.forClass(SearchRequest.class);
        verify(vectorStore).similaritySearch(searchRequest.capture());
        assertThat(searchRequest.getValue().getQuery()).isEqualTo("What is RAG?");
        assertThat(searchRequest.getValue().getTopK()).isEqualTo(3);

        ArgumentCaptor<String> prompt = ArgumentCaptor.forClass(String.class);
        verify(promptSpec).user(prompt.capture());
        assertThat(prompt.getValue())
                .contains("RAG retrieves relevant documents")
                .contains("What is RAG?");
    }

    @Test
    void tellsTheModelWhenNoRelevantDocumentsAreFound() {
        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(List.of());
        ChatClient.ChatClientRequestSpec promptSpec = chatClient.prompt();
        when(promptSpec.user(anyString()).call().content())
                .thenReturn("The available documents do not provide that information.");
        clearInvocations(chatClient, promptSpec);

        assertThat(service.answer("What is the release date?"))
                .isEqualTo("The available documents do not provide that information.");

        ArgumentCaptor<String> prompt = ArgumentCaptor.forClass(String.class);
        verify(promptSpec).user(prompt.capture());
        assertThat(prompt.getValue()).contains("No relevant documents were found.");
    }

    @Test
    void convertsProviderFailureToControlledApplicationException() {
        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(List.of());
        when(chatClient.prompt()).thenThrow(new IllegalStateException("provider secret"));

        assertThatThrownBy(() -> service.answer("What is RAG?"))
                .isInstanceOf(ModelRequestException.class)
                .hasMessage("The AI model request failed")
                .hasCauseInstanceOf(IllegalStateException.class);
    }

    @Test
    void rejectsBlankModelContent() {
        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(List.of());
        when(chatClient.prompt().user(anyString()).call().content()).thenReturn("   ");

        assertThatThrownBy(() -> service.answer("What is RAG?"))
                .isInstanceOf(ModelRequestException.class)
                .hasMessage("The AI model did not return an answer");
    }

    @Test
    void convertsEmbeddingSearchFailureToControlledApplicationException() {
        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenThrow(new IllegalStateException("embedding provider secret"));

        assertThatThrownBy(() -> service.answer("What is RAG?"))
                .isInstanceOf(ModelRequestException.class)
                .hasMessage("The AI model request failed")
                .hasCauseInstanceOf(IllegalStateException.class);
    }
}
