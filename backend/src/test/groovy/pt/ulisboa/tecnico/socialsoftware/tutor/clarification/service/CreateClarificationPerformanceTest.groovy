package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.Doubt
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.*
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.DoubtRepositor
import spock.lang.Unroll
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class CreateClarificationPerformanceTest extends Specification {

    public static final String COURSE_NAME = "Software Architecture"
    public static final String QUESTION_CONTENT = 'question content'
    public static final Integer QUESTION_KEY = 52
    public static final String QUESTION_TITLE = 'question title'
    public static final String USER_NAME = "user"
    public static final String USERNAME_NAME = "username"
    public static final Integer USER_KEY = 90000
    public static final String USER2_NAME = "user2"
    public static final String USERNAME2_NAME = "username2"
    public static final Integer USER2_KEY = 10000
    public static final String DOUBT_CONTENT = "doubt content"
    public static final String CLARIFICATION_DESCRIPTION = "Your answer isn't correct because you need to watch the videos."
    public static final String ACRONYM = "C12"
    public static final String ACADEMIC_TERM = "1º Semestre"

    @Autowired
    ClarificationService clarificationService

    @Autowired
    DoubtRepositor doubtRepositor

    @Autowired
    CourseRepository courseRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    def setup() {}

    def "performance testing to create 1000 clarifications"() {

        given: "a course"
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)

        and: "a question"
        def questionDto = new QuestionDto()
        questionDto.setKey(QUESTION_KEY)
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        def optionDto = new OptionDto()
        optionDto.setContent(QUESTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setOptions(options)
        def question = new Question(course, questionDto)
        questionRepository.save(question)

        and: "a student"
        def student  = new User(USER_NAME, USERNAME_NAME, USER_KEY, User.Role.STUDENT)
        userRepository.save(student)

        and: "a teacher"
        def teacher = new User(USER2_NAME, USERNAME2_NAME, USER2_KEY, User.Role.TEACHER)
        userRepository.save(teacher)
        courseExecution.addUser(teacher)
        teacher.addCourse(courseExecution)
        courseExecutionRepository.save(courseExecution)

        when: " creates a doubt and the respective clarification"
        1.upto(1000, {
            def doubt = new Doubt(question,student,DOUBT_CONTENT);
            doubtRepositor.save(doubt)
            def clarificationDto = new ClarificationDto()
            clarificationDto.setDescription(CLARIFICATION_DESCRIPTION)
            clarificationService.createClarification(clarificationDto,doubt.getId(),teacher.getId())
        })

        then:
        true
    }

    @TestConfiguration
    static class ClarificationServiceImplTestContextConfiguration {

        @Bean
        ClarificationService clarificationService() {
            return new ClarificationService()
        }

    }

}



