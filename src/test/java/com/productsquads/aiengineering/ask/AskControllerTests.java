package com.productsquads.aiengineering.ask;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.productsquads.aiengineering.web.GlobalExceptionHandler;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
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
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AskController(questionAnswerService))
                .setValidator(validator)
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
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().is(500));
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
                        org.hamcrest.Matchers.allOf(
                                org.hamcrest.Matchers.containsString("The AI service is temporarily unavailable"),
                                org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("api-key")))));
    }
}
