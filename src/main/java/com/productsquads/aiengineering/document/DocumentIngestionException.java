package com.productsquads.aiengineering.document;

public class DocumentIngestionException extends RuntimeException {

    public DocumentIngestionException(Throwable cause) {
        super("Document embedding failed", cause);
    }
}
