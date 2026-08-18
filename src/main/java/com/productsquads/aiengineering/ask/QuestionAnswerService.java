package com.productsquads.aiengineering.ask;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class QuestionAnswerService {

    private static final int MAX_RETRIEVED_DOCUMENTS = 3;
    private static final String NO_CONTEXT = "No relevant documents were found.";
    private static final String GROUNDED_PROMPT = """
            Answer the question using only the retrieved context below.
            If the context does not contain the answer, say that the available documents do not provide it.

            Retrieved context:
            %s

            Question:
            %s
            """;

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Autowired
    public QuestionAnswerService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this(chatClientBuilder.build(), vectorStore);
    }

    QuestionAnswerService(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    public String answer(String question) {
        final String answer;
        try {
            List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder()
                    .query(question)
                    .topK(MAX_RETRIEVED_DOCUMENTS)
                    .build());
            String context = buildContext(documents);

            answer = chatClient.prompt()
                    .user(GROUNDED_PROMPT.formatted(context, question))
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

    private static String buildContext(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return NO_CONTEXT;
        }
        String context = documents.stream()
                .map(Document::getText)
                .filter(StringUtils::hasText)
                .collect(Collectors.joining("\n\n---\n\n"));
        return StringUtils.hasText(context) ? context : NO_CONTEXT;
    }
}
