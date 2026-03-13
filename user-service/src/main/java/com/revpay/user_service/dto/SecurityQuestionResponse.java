package com.revpay.user_service.dto;

import com.revpay.user_service.enums.SecurityQuestion;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SecurityQuestionResponse {

    private String key;
    private String question;

    public SecurityQuestionResponse() {}

    public SecurityQuestionResponse(String key, String question) {
        this.key = key;
        this.question = question;
    }

    // Static method to get all questions for dropdown
    public static List<SecurityQuestionResponse> getAllQuestions() {
        return Arrays.stream(SecurityQuestion.values())
                .map(q -> new SecurityQuestionResponse(
                        q.name(), q.getQuestion()))
                .collect(Collectors.toList());
    }

    // Getters
    public String getKey() { return key; }
    public String getQuestion() { return question; }

    // Setters
    public void setKey(String key) { this.key = key; }
    public void setQuestion(String question) { this.question = question; }
}