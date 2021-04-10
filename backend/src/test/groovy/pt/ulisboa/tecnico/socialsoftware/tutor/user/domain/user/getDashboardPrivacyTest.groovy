package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.DiscussionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.DoubtRepositor
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.DoubtService
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DISCUSSION_IS_EMPTY
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DISCUSSION_USER_IS_EMPTY
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DOUBT_USER_IS_NOT_A_STUDENT
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_IS_EMPTY
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_IS_NOT_STUDENT

@DataJpaTest
class getDashboardPrivacyTest extends Specification {
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
    public static final String STUDENT_NAME = "student"
    public static final String STUDENT_USERNAME = "student username"
    public static final Integer STUDENT_KEY = 3
    public static final Integer USER_KEY = 90000
    public static final String DOUBT_CONTENT = "doubt content"
    public static final String DOUBT_TITLE = "doubt title"
    public static final String DOUBT2_CONTENT = "doubt2 content"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"


    @Autowired
    UserRepository userRepository


    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository


    @Autowired
    UserService userService

    def course
    def course2
    def student2
    def student
    def courseExecution
    def courseExecution2
    def teacher


    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        course2 = new Course(COURSE2_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        courseRepository.save(course2)


        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        courseExecution2 = new CourseExecution(course2, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution2)


        student = new User(USER_NAME, USERNAME_NAME, USER_KEY, User.Role.STUDENT)
        student.courseExecutions.add(courseExecution)
        student2 = new User(STUDENT_NAME, STUDENT_USERNAME, STUDENT_KEY, User.Role.STUDENT)
        student2.courseExecutions.add(courseExecution)
        teacher = new User("Teacher name", "Teacher Username", 292, User.Role.TEACHER)
        student.setDashboardPrivacy(false);
        student2.setDashboardPrivacy(true);
        userRepository.save(student)
        userRepository.save(student2)
        userRepository.save(teacher)



    }

    def "make a Dashboard private"() {
        when: "You change DashBoardPrivacy of a Student who has it public"
        def result = userService.getDashBoardPrivacy(student.getId())

        then:
        !result.isDashboardPrivacy();
    }

    def "make a Dahsboard public"() {
        when: "You change DashBoardPrivacy of a Student who has it private"
        def result = userService.getDashBoardPrivacy(student2.getId())

        then:
        result.isDashboardPrivacy();
    }

    def "change privacy on a null user"() {
        when: "You change the privacy of DashBoard"
        userService.getDashBoardPrivacy(null)

        then:
        def exception = thrown(TutorException)
        exception.errorMessage == USER_IS_EMPTY;

    }

    def "changing privacy but user is not student"(){
        when: "You change the privacy of DashBoard"
        userService.getDashBoardPrivacy(teacher.getId())

        then:
        def exception = thrown(TutorException)
        exception.errorMessage == USER_IS_NOT_STUDENT

    }

    @TestConfiguration
    static class UserServiceImplTestContextConfiguration {

        @Bean
        UserService userService() {
            return new UserService()
        }

        @Bean
        QuestionService questionService(){
            return new QuestionService()
        }

    }
}
