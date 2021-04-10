package pt.ulisboa.tecnico.socialsoftware.tutor.doubt.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.DoubtDto
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.DoubtRepositor
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.DoubtService
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class CreateDoubtPerformanceTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String COURSE2_NAME = "Distributed Systems"
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION2_TITLE = 'question2 title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String QUESTION2_CONTENT = 'question2 content'
    public static final String QUIZ_TITLE = "quiz title"
    public static final Integer QUIZ_SERIES = 1
    public static final Integer QUIZ_KEY = 2
    public static final Integer QUESTION_KEY = 52
    public static final Integer QUESTION2_KEY = 54
    public static final String USER_NAME = "user"
    public static final String USERNAME_NAME = "username"
    public static final String TEACHER_NAME = "teacher"
    public static final String TEACHER_USERNAME = "teacher username"
    public static final Integer TEACHER_KEY = 3
    public static final Integer USER_KEY = 90000
    public static final String DOUBT_CONTENT = "doubt content"
    public static final String DOUBT_TITLE = "doubt title"


    @Autowired
    DoubtService doubtService

    @Autowired
    DoubtRepositor doubtRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    def question
    def questiondto
    def optiondto
    def course
    def course2
    def quiz
    def quizdto
    def student
    def options
    def quizquestion
    def quizanswer
    def questionanswer


    def setup() {
        quizdto = new QuizDto()
        quizdto.setTitle(QUIZ_TITLE)
        quizdto.setSeries(QUIZ_SERIES)
        quizdto.setKey(QUIZ_KEY)
        quizdto.setScramble(true)
        quizdto.setType(Quiz.QuizType.GENERATED)
        quiz = new Quiz(quizdto)
        quizRepository.save(quiz)
        student  = new User(USER_NAME, USERNAME_NAME, USER_KEY, User.Role.STUDENT)
        userRepository.save(student)

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        course2 = new Course(COURSE2_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        courseRepository.save(course2)

        questiondto = new QuestionDto()
        questiondto.setKey(QUESTION_KEY)
        questiondto.setTitle(QUESTION_TITLE)
        questiondto.setContent(QUESTION_CONTENT)
        questiondto.setStatus(Question.Status.AVAILABLE.name())

        optiondto = new OptionDto()
        optiondto.setContent(QUESTION_CONTENT)
        optiondto.setCorrect(true)
        options = new ArrayList<OptionDto>()
        options.add(optiondto)
        questiondto.setOptions(options)

        question = new Question(course, questiondto)

        questionRepository.save(question)

        quizanswer = new QuizAnswer(student, quiz)
        quizAnswerRepository.save(quizanswer)

        quizquestion = new QuizQuestion(quiz, question, 1)
        quizQuestionRepository.save(quizquestion)

        questionanswer = new QuestionAnswer(quizanswer,quizquestion,30);
        questionAnswerRepository.save(questionanswer);


    }

    def "performance testing to creating 1000 doubts"() {
        given: "an emptyDoubtDto"
        def doubtdto

        when: "1000 doubts are created"
        1.upto(1000, {
            doubtdto = new DoubtDto()
            doubtdto.setContent(DOUBT_CONTENT + it)
            doubtService.createDoubt(doubtdto, questionanswer.getId(), student.getId())
        })

        then:
        true
    }





    @TestConfiguration
    static class DoubtServiceImplTestContextConfiguration {

        @Bean
        DoubtService doubtService() {
            return new DoubtService()
        }

    }

}
