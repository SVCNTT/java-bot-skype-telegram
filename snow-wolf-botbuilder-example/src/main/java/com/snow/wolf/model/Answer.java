package com.snow.wolf.model;

public class Answer {
    public Question question;
    public String answerText;

    public Answer() {

    }


    public Answer(Question question) {
        this.question = question;
    }
}
