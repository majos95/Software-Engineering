package pt.ulisboa.tecnico.socialsoftware.tutor.doubt.service

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
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.Discussion
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.DiscussionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.DoubtService
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.DoubtRepositor

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.Doubt
import spock.lang.Specification
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class GetUserDiscussionListTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String USER_NAME = "user"
    public static final String USERNAME_NAME = "username"
    public static final Integer USER_KEY = 90000
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final Integer QUESTION_KEY = 3
    public static final String DOUBT_CONTENT = 'doubt content'
    public static final String DOUBT_TITLE = 'doubt title'
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final Integer QUIZ_KEY = 2



    @Autowired
    DoubtService doubtService

    @Autowired
    UserRepository userRepository

    @Autowired
    DoubtRepositor doubtRepositor

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    ClarificationService clarificationService

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    DiscussionRepository discussionRepository

    def student
    def question
    def questiondto
    def optiondto
    def options
    def course
    def courseExecution
    def quiz
    def quizquestion
    def quizanswer
    def questionanswer


    def setup(){
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)


        quiz = new Quiz()
        quiz.setKey(QUIZ_KEY)
        quiz.setType(Quiz.QuizType.GENERATED.toString())
        quiz.setCourseExecution(courseExecution)
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)

        student  = new User(USER_NAME, USERNAME_NAME, USER_KEY, User.Role.STUDENT)
        userRepository.save(student)

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
        student.addQuizAnswer(quizanswer)

        quizquestion = new QuizQuestion(quiz, question, 1)
        questionanswer = new QuestionAnswer(quizanswer, quizquestion, 30);
        quizanswer.addQuestionAnswer(questionanswer);
        quizquestion.addQuestionAnswer(questionanswer);

        quizQuestionRepository.save(quizquestion)
        questionAnswerRepository.save(questionanswer)
        quizAnswerRepository.save(quizanswer)


    }

    def "Get the doubt list of a user that has no doubts"(){

        when:
        def result = doubtService.findUserDiscussions(student.getId())

        then:
        result.size() == 0
    }

    def "Get the doubt list of a null user"(){

        when:
        def result = doubtService.findUserDiscussions(null)

        then:
        def error = thrown(TutorException)
        error.errorMessage == DOUBT_USER_IS_EMPTY

    }

    def "Get the doubt list of a user"(){

        given: "A doubt"
        def discussion = new Discussion(questionanswer, DOUBT_TITLE, student)
        discussionRepository.save(discussion)

        when:
        def result = doubtService.findUserDiscussions(student.getId())

        then:
        result.size() == 1
        def newDiscussion = result.get(0);
        newDiscussion.getAuthor() == student.getName()
        newDiscussion.getTitle() == DOUBT_TITLE
    }



    @TestConfiguration
    static class DoubtServiceImplTestContextConfiguration {

        @Bean
        DoubtService doubtService() {
            return new DoubtService()
        }

        @Bean
        ClarificationService clarificationService(){
            return new ClarificationService()
        }
    }


}
