package pt.ulisboa.tecnico.socialsoftware.tutor.doubt.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.DoubtService
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.DoubtRepositor

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.Doubt
import spock.lang.Specification
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class GetUserDoubtsListPerformanceTest extends Specification {

    public static final String COURSE_NAME = "Software Architecture"
    public static final String QUESTION_CONTENT = 'question content'
    public static final Integer QUESTION_KEY = 52
    public static final String QUESTION_TITLE = 'question title'
    public static final String USER_NAME = "user"
    public static final String USERNAME_NAME = "username"
    public static final Integer USER_KEY = 90000
    public static final String DOUBT_CONTENT = "doubt content"

    @Autowired
    DoubtService doubtService

    @Autowired
    DoubtRepositor doubtRepositor

    @Autowired
    CourseRepository courseRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    UserRepository userRepository

    def setup() {}

    def "performance testing to get 1000 doubts"() {

        given: "a course"
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

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

        and: "1000 doubts"
        1.upto(1000,{
            doubtRepositor.save(new Doubt(question,student,DOUBT_CONTENT))
        })

        when:
        1.upto(1000,{ doubtRepositor.findUserDoubts(student.getId()) })

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
