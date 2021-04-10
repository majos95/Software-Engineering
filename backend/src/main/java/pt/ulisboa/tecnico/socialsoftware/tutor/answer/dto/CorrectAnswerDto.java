package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;

import java.io.Serializable;

public class CorrectAnswerDto implements Serializable {
    private Integer quizQuestionId;
    private Integer correctOptionId;
    private Integer sequence;

    public CorrectAnswerDto(QuestionAnswer questionAnswer) {
        this.quizQuestionId = questionAnswer.getQuizQuestion().getId();
        this.correctOptionId = questionAnswer.getQuizQuestion().getQuestion().getCorrectOptionId();
        this.sequence = questionAnswer.getSequence();
    }

    public Integer getCorrectOptionId() {
        return correctOptionId;
    }

    public void setCorrectOptionId(Integer correctOptionId) {
        this.correctOptionId = correctOptionId;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getQuizQuestionId() {
        return quizQuestionId;
    }

    public void setQuizQuestionId(Integer quizQuestionId) {
        this.quizQuestionId = quizQuestionId;
    }

    @Override
    public String toString() {
        return "CorrectAnswerDto{" +
                " quizQuestionId" + quizQuestionId +
                ", correctOptionId=" + correctOptionId +
                ", sequence=" + sequence +
                '}';
    }
}