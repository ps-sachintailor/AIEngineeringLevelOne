package com.productsquads.aiengineering.ask;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.productsquads.aiengineering.web.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AskControllerTests {

    private QuestionAnswerService questionAnswerService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        questionAnswerService = mock(QuestionAnswerService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AskController(questionAnswerService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void validQuestionReturnsGeneratedAnswer() throws Exception {
        when(questionAnswerService.answer("What is RAG?"))
                .thenReturn("Retrieval-augmented generation combines retrieval with generation.");

        mockMvc.perform(post("/ask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\":\"What is RAG?\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value(
                        "Retrieval-augmented generation combines retrieval with generation."));
    }

    @Test
    void missingQuestionReturnsClearValidationError() throws Exception {
        mockMvc.perform(post("/ask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.fieldErrors.question").value("question is required"));
    }

    @Test
    void emptyQuestionReturnsClearValidationError() throws Exception {
        mockMvc.perform(post("/ask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.question").value("question is required"));
    }

    @Test
    void whitespaceQuestionReturnsClearValidationError() throws Exception {
        mockMvc.perform(post("/ask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.question").value("question is required"));
    }

    @Test
    void modelFailureReturnsControlledErrorWithoutProviderDetails() throws Exception {
        when(questionAnswerService.answer("What is RAG?"))
                .thenThrow(new ModelRequestException(
                        new IllegalStateException("api-key=must-not-be-returned")));

        mockMvc.perform(post("/ask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\":\"What is RAG?\"}"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.message").value(
                        "The AI service is temporarily unavailable. Please try again later."))
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("api-key"))));
    }
}
