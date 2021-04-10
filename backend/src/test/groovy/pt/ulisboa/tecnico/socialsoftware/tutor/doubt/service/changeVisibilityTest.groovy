package pt.ulisboa.tecnico.socialsoftware.tutor.doubt.service

import org.h2.store.DataHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.Discussion
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.DiscussionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.Doubt
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.DoubtDto
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
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.DoubtRepositor

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

import java.time.format.DateTimeFormatter

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*


import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime


@DataJpaTest
class changeVisibilityTest extends Specification {
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
    public static final String DOUBT2_CONTENT = "doubt2 content"


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
    ClarificationService clarificationService

    @Autowired
    DiscussionRepository discussionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    def question
    def question2
    def questiondto
    def questiondto2
    def optiondto
    def course
    def course2
    def quiz
    def quizdto
    def teacher
    def student
    def options
    def quizquestion
    def quizquestion2
    def quizanswer
    def discussion
    def doubt
    def questionanswer


    def setup() {
        quizdto = new QuizDto()
        quizdto.setTitle(QUIZ_TITLE)
        quizdto.setSeries(QUIZ_SERIES)
        quizdto.setKey(QUIZ_KEY)
        quizdto.setScramble(true)
        quizdto.setType(Quiz.QuizType.GENERATED as String)
        String availableD = DateHandler.toISOString(LocalDateTime.now())
        String dateString = "2021-07-14T17:45:55.9483536";
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
        String conclusionD = DateHandler.toISOString(dateTime)
        quizdto.setAvailableDate(availableD)
        quizdto.setConclusionDate(conclusionD)
        quiz = new Quiz(quizdto)
        quizRepository.save(quiz)
        student = new User(USER_NAME, USERNAME_NAME, USER_KEY, User.Role.STUDENT)
        teacher = new User(TEACHER_NAME, TEACHER_USERNAME, TEACHER_KEY, User.Role.TEACHER)
        userRepository.save(student)
        userRepository.save(teacher)

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

        questiondto2 = new QuestionDto()
        questiondto2.setKey(QUESTION2_KEY)
        questiondto2.setTitle(QUESTION2_TITLE)
        questiondto2.setContent(QUESTION2_CONTENT)
        questiondto2.setStatus(Question.Status.AVAILABLE.name())

        questiondto2.setOptions(options)

        question = new Question(course, questiondto)
        question2 = new Question(course2, questiondto2)

        questionRepository.save(question)
        questionRepository.save(question2)

        quizanswer = new QuizAnswer(student, quiz)
        quizAnswerRepository.save(quizanswer)




        quizquestion = new QuizQuestion(quiz, question, 1)
        quizquestion2 = new QuizQuestion(quiz, question2, 2)
        quizQuestionRepository.save(quizquestion)
        quizQuestionRepository.save(quizquestion2)
        questionanswer = new QuestionAnswer(quizanswer,quizquestion,2)
        questionAnswerRepository.save(questionanswer)
        discussion = new Discussion(questionanswer, "Bom Dia", student)
        discussionRepository.save(discussion)



    }

    def "public doubt"() {
        given: "a DoubtDto"
        doubt = new Doubt(student,DateHandler.now().toString(),DOUBT_CONTENT,true,discussion)
        discussion.addPost(doubt)
        doubtRepository.save(doubt)
        when: "a doubt is set to public"

        then:
        doubtService.changeVisibility(discussion.getId(), Discussion.Visibility.PUBLIC);
        discussionRepository.count() == 1L
        def result = discussionRepository.findAll().get(0)

        result.getVisibility() == Discussion.Visibility.PUBLIC
    }

    @TestConfiguration
    static class DoubtServiceImplTestContextConfiguration {

        @Bean
        DoubtService doubtService() {
            return new DoubtService()
        }

        @Bean
        ClarificationService clarificationService() {
            return new ClarificationService()
        }
    }

}