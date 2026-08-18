package com.productsquads.aiengineering;

import com.productsquads.aiengineering.config.AiModelProperties;
import com.productsquads.aiengineering.config.RagInfrastructureProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AiModelProperties.class, RagInfrastructureProperties.class})
public class RagApplication {

    public static void main(String[] args) {
        SpringApplication.run(RagApplication.class, args);
    }
}
