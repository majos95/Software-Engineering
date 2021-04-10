package pt.ulisboa.tecnico.socialsoftware.tutor.evaluation;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;

//trying to merge

public class EvaluationDto {

    private boolean approvedEvaluation = false;

    private Integer id;

    private Integer key;

    private String justification;

    private String teacherUsername;

    private QuestionDto submittedQuestion;

    public EvaluationDto() {

    }

    public EvaluationDto(Evaluation evaluation) {
        if (evaluation.getEvaluation()) {
            approvedEvaluation = true;
        }
        this.id = evaluation.getId();
        this.justification = evaluation.getJustification();
    }

    public void setId(int questionId) { id = questionId; }

    public Integer getId() { return id; }

    public void setKey(int questionKey) { key = questionKey; }

    public Integer getKey() { return key; }

    public QuestionDto getSubmittedQuestionDto() { return submittedQuestion; }

    public void setSubmittedQuestionDto(QuestionDto questionDto) {
        this.submittedQuestion = questionDto;
        this.setKey(this.submittedQuestion.getKey());
    }

    public boolean getApprovedEvaluation() { return this.approvedEvaluation;}

    public String getTeacherUsername() { return this.teacherUsername;}

    public void setTeacherUsername(String username) { teacherUsername = username; }

    public void setJustification(String message) { justification = message; }

    public void approveEvaluationDto() { approvedEvaluation = true; }

    public String getJustification() { return  this.justification; }
}
