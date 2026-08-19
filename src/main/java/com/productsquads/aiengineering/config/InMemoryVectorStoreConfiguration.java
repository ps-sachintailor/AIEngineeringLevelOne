package com.productsquads.aiengineering.config;



import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;

@Configuration(proxyBeanMethods = false)
@Profile({"local-rag", "remote-rag"})
public class InMemoryVectorStoreConfiguration {

    @Value("classpath:vector-store.json")
    private Resource resourceFile;  
    
    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        //File file = new File(storageFilePath);

        // 1. Create the vector store using the current builder API
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();

        // 2. Load existing embeddings from disk if the file exists on startup
         if (resourceFile.exists()) {
            try {
                vectorStore.load(resourceFile);
                System.out.println(">>> Persistent vectors loaded successfully from " + resourceFile.getFilename());
            } catch (Exception e) {
                System.err.println(">>> Failed to load existing vectors: " + e.getMessage());
            }
        }

        return vectorStore;
    }


}
