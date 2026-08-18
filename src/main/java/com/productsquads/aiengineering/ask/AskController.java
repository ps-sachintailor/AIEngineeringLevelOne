package com.productsquads.aiengineering.ask;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AskController {

    private final QuestionAnswerService questionAnswerService;

    public AskController(QuestionAnswerService questionAnswerService) {
        this.questionAnswerService = questionAnswerService;
    }

    @PostMapping(
            path = "/ask",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    AskResponse ask(@Valid @RequestBody AskRequest request) {
        return new AskResponse(questionAnswerService.answer(request.question()));
    }
}
