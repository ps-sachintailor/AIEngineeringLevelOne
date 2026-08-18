package com.productsquads.aiengineering.ask;

public class ModelRequestException extends RuntimeException {

    public ModelRequestException() {
        super("The AI model did not return an answer");
    }

    public ModelRequestException(Throwable cause) {
        super("The AI model request failed", cause);
    }
}
