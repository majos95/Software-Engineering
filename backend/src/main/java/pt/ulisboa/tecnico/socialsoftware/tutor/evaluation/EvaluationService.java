package pt.ulisboa.tecnico.socialsoftware.tutor.evaluation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;


import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;
//trying to merge

@Service
public class EvaluationService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    EntityManager entityManager;

    EvaluationService() {
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public EvaluationDto findEvaluationByKey(int questionId){
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
        Evaluation evaluation = evaluationRepository.findByKey(question.getKey()).orElseThrow(() -> new TutorException(EVALUATION_NOT_AVAILABLE, question.getKey()));
        EvaluationDto evaluationDto = new EvaluationDto(evaluation);
        return evaluationDto;
    }
    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public EvaluationDto createEvaluation(EvaluationDto evaluationDto, QuestionDto questionDto) {
        Question question = questionRepository.findByKey(questionDto.getKey()).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionDto.getKey()));
        checkIfPending(question);

        Evaluation evaluation = new Evaluation(question);
        this.entityManager.persist(evaluation);
        EvaluationDto evaluationDto1 = new EvaluationDto(evaluation);
        evaluationDto1.setSubmittedQuestionDto(questionDto);
        return evaluationDto1;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public EvaluationDto submitEvaluation(String teacherUsername, Integer questionId, EvaluationDto evaluationDto) {
        User teacher = userRepository.findByUsername(teacherUsername);
        if(teacher == null) {
            throw new TutorException(USERNAME_NOT_FOUND, teacherUsername);
        }
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
        Evaluation evaluation = evaluationRepository.findByKey(question.getKey()).orElseThrow(() -> new TutorException(EVALUATION_NOT_AVAILABLE, question.getKey()));

        if (evaluationDto.getApprovedEvaluation()) {
            question.setStatus(Question.Status.DISABLED);
            QuestionDto questionDto = new QuestionDto(question);
            evaluationDto.setSubmittedQuestionDto(questionDto);
            evaluation.approveEvaluation();
            evaluation.setJustification(evaluationDto.getJustification());
        }
        else {
            question.setStatus(Question.Status.REJECTED);
            QuestionDto questionDto = new QuestionDto(question);
            evaluationDto.setSubmittedQuestionDto(questionDto);
            if (evaluationDto.getJustification() == null || evaluationDto.getJustification().length() == 0){
                throw new TutorException(MUST_HAVE_JUSTIFICATION);
            }
            evaluation.setJustification(evaluationDto.getJustification());
        }
        evaluation.setTeacherUsername(teacherUsername);
        EvaluationDto evaluationDto1 = new EvaluationDto(evaluation);
        evaluationDto1.setSubmittedQuestionDto(evaluationDto.getSubmittedQuestionDto());
        evaluationDto1.setTeacherUsername(teacherUsername);
        return evaluationDto1;
    }

    private void checkIfPending(Question question) {
        if (question.getStatus() != Question.Status.PENDING){
            throw new TutorException(QUESTION_NOT_PENDING, question.getKey());
        }
    }


}
