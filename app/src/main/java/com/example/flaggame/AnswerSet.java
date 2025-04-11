package com.example.flaggame;


import java.util.List;

public class AnswerSet {

    private final Flag rightAnswer;
    private final List<Flag> answersChoices;

    public AnswerSet(Flag rightAnswer, List<Flag> answersChoices) {
        this.rightAnswer = rightAnswer;
        this.answersChoices = answersChoices;
    }


    public Flag getRightAnswer() {
        return rightAnswer;
    }


    public List<Flag> getAnswersChoices() {
        return answersChoices;
    }

}
