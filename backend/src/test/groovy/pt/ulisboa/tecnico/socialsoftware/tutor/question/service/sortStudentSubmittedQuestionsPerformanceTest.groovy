package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class sortStudentSubmittedQuestionsPerformanceTest extends Specification {
    public static final String QUESTION_TITLE = "QuestionTitleTEST"
    public static final String QUESTION_CONTENT = "QuestionContentTEST"
    public Integer QUESTION_KEY = 99999
    public static final String OPTION_CONTENT = "OptionContentTEST"

    public static final String COURSE_NAME = "CourseNameTEST"
    public static final Integer COURSE_ID = 3

    public static final String STUDENT_NAME = "StudentNameTEST"
    public static final String STUDENT_USERNAME = "StudentUsernameTEST"
    public static final Integer STUDENT_KEY = 1
    public static final Integer STUDENT_ID = 6969

    @Autowired
    QuestionService questionService

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    UserRepository userRepository

    def "performance testing to submit 1000 questions and getting sorted by creation date"() {
        given: "a course"
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        and: "a student"
        def student = new User(STUDENT_NAME, STUDENT_USERNAME, STUDENT_KEY, User.Role.STUDENT)
        userRepository.save(student)

        and: "an option (list)"
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        and: "1000 submitted questions"
        1.upto(2, {
            def questionDto = new QuestionDto()
            questionDto.setKey(QUESTION_KEY)
            questionDto.setTitle(QUESTION_TITLE)
            questionDto.setContent(QUESTION_CONTENT)
            questionDto.setStatus(Question.Status.PENDING.name())
            questionDto.setOptions(options)
            QUESTION_KEY += 1

            questionService.submitQuestion(student.getId(), course.getId(), questionDto)
        })

        when:
        1.upto(20, {questionService.sortStudentSubmittedQuestionsByCreationDate(student.getUsername())})

        then:
        true
    }

    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
}
