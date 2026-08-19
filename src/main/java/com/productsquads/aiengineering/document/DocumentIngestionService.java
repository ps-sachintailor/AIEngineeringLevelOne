package com.productsquads.aiengineering.document;

import java.util.List;
import java.util.Map;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class DocumentIngestionService {

    private final SimpleVectorStore vectorStore; 


    public DocumentIngestionService(SimpleVectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public DocumentIngestionResponse ingest(DocumentIngestionRequest request) {
        Map<String, Object> metadata = request.metadata() == null
                ? Map.of()
                : request.metadata();
        Document document = new Document(request.content(), metadata);

        try {
            vectorStore.add(List.of(document));
            vectorStore.save(new File("src/main/resources/vector-store.json")); 
    
        } catch (RuntimeException exception) {
            throw new DocumentIngestionException(exception);
        }

        return new DocumentIngestionResponse(document.getId(), "embedded");
    }
}
