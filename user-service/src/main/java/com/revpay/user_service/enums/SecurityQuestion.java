package com.revpay.user_service.enums;

public enum SecurityQuestion {
    MOTHERS_MAIDEN_NAME("What is your mother's maiden name?"),
    FIRST_PET_NAME("What was the name of your first pet?"),
    CHILDHOOD_CITY("In what city did you grow up?"),
    FIRST_SCHOOL("What was the name of your first school?"),
    FAVORITE_TEACHER("What was the name of your favorite teacher?"),
    FIRST_CAR("What was the make of your first car?");

    private final String question;

    SecurityQuestion(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }
}