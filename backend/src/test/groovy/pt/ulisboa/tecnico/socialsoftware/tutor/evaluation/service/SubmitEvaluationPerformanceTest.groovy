package pt.ulisboa.tecnico.socialsoftware.tutor.evaluation.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.evaluation.EvaluationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.evaluation.EvaluationService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
//trying to merge

@DataJpaTest
class SubmitEvaluationPerformanceTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"

    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content1"

    public static final Integer QUESTION_KEY = 5000

    public static final String JUSTIFICATION = "Question Justification"
    public static final String USERNAME = "ist12627"
    public static final String PERSON_NAME = "NAME"

    @Autowired
    QuestionService questionService

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    EvaluationService evaluationService


    def "performance testing to submit 10000 evaluations"() {
        given: 'a teacher'
        def user = new User()
        user.setKey(1)
        user.setUsername(USERNAME)
        user.setName(PERSON_NAME)
        user.setRole(User.Role.TEACHER)
        userRepository.save(user)

        when:
        1.upto(2,{
            def evaluationDto = new EvaluationDto()
            def pendingQuestion = new Question()
            pendingQuestion.setStatus(Question.Status.PENDING)
            pendingQuestion.setKey(QUESTION_KEY + it)
            questionRepository.save(pendingQuestion)
            def pendingQuestionDto = new QuestionDto(pendingQuestion)
            def evaluationDto1 = evaluationService.createEvaluation(evaluationDto, pendingQuestionDto)
            evaluationDto1.setJustification(JUSTIFICATION)
            def questionId = pendingQuestion.getId()
            evaluationService.submitEvaluation(user.getUsername(), questionId, evaluationDto1)
        })

        then:
        true
    }


    @TestConfiguration
    static class EvaluationServiceImplTestContextConfiguration {

        @Bean
        EvaluationService evaluationService() {
            return new EvaluationService()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
}
