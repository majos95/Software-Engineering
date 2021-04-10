package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class CalculateApprovedVersusProposedTest extends Specification {

    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'

    public static final String QUESTION_TITLE2 = 'question title2'
    public static final String QUESTION_CONTENT2 = 'question content2'

    public static final String STUDENT_NAME = "Student Name"
    public static final String STUDENT_USERNAME = "StudentUsername"
    public static final Integer STUDENT_KEY = 1

    @Autowired
    UserRepository userRepository

    @Autowired
    QuestionService questionService

    User student

    def setup() {
        student = new User(STUDENT_NAME, STUDENT_USERNAME, STUDENT_KEY, User.Role.STUDENT)
        userRepository.save(student)
    }

    def "student didn't propose no questions" (){
        when:
        List<Integer> stats = questionService.calculateApprovedVersusProposed(student.getUsername())

        then:
        stats.size() == 2
        stats[0] == 0 // No proposed
        stats[1] == 0 // No approved
    }

    def "student proposes 2 question" () {
        given: "2 question"
        def question = new Question()
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(Question.Status.PENDING)
        def question2 = new Question()
        question2.setTitle(QUESTION_TITLE2)
        question2.setContent(QUESTION_CONTENT2)
        question2.setStatus(Question.Status.REJECTED)
        student.addSubmittedQuestion(question)
        student.addSubmittedQuestion(question2)

        when:
        List<Integer> stats = questionService.calculateApprovedVersusProposed(student.getUsername())

        then:
        stats.size() == 2
        stats[0] == 2
        stats[1] == 0
    }

    def "student proposes 2 questions and are approved" () {
        given: "2 question"
        def question = new Question()
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(Question.Status.DISABLED)
        def question2 = new Question()
        question2.setTitle(QUESTION_TITLE2)
        question2.setContent(QUESTION_CONTENT2)
        question2.setStatus(Question.Status.AVAILABLE)
        student.addSubmittedQuestion(question)
        student.addSubmittedQuestion(question2)

        when:
        List<Integer> stats = questionService.calculateApprovedVersusProposed(student.getUsername())

        then:
        stats.size() == 2
        stats[0] == 2
        stats[1] == 2
    }

    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
}
