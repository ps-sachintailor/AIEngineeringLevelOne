package com.productsquads.aiengineering.document;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/documents", produces = MediaType.APPLICATION_JSON_VALUE)
public class DocumentIngestionController {

    private final DocumentIngestionService documentIngestionService;

    public DocumentIngestionController(DocumentIngestionService documentIngestionService) {
        this.documentIngestionService = documentIngestionService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<DocumentIngestionResponse> ingest(
            @Valid @RequestBody DocumentIngestionRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(documentIngestionService.ingest(request));
    }
}
