package com.productsquads.aiengineering.document;

import java.util.List;
import java.util.Map;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class DocumentIngestionService {

    private final VectorStore vectorStore;

    public DocumentIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public DocumentIngestionResponse ingest(DocumentIngestionRequest request) {
        Map<String, Object> metadata = request.metadata() == null
                ? Map.of()
                : request.metadata();
        Document document = new Document(request.content(), metadata);

        try {
            vectorStore.add(List.of(document));
        } catch (RuntimeException exception) {
            throw new DocumentIngestionException(exception);
        }

        return new DocumentIngestionResponse(document.getId(), "embedded");
    }
}
