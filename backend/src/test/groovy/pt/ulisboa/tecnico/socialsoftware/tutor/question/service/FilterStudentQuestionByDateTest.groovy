package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USERNAME_NOT_FOUND

@DataJpaTest
class FilterStudentQuestionByDateTest extends Specification {
    public static final String QUESTION_TITLE_1 = "question title1"
    public static final String QUESTION_TITLE_2 = "question title2"
    public static final String QUESTION_TITLE_3 = "question title3"
    public static final String STUDENT_NAME = "student name"
    public static final String STUDENT_USERNAME1 = "student username"
    public static final String STUDENT_USERNAME2 = "student username2"
    public static final Integer STUDENT_KEY = 1
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_CONTENT = "question content"
    public static final String OPTION_CONTENT = "optionId content"

    @Autowired
    QuestionService questionService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    def course
    def student
    def courseExecution

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        student = new User(STUDENT_NAME, STUDENT_USERNAME1, STUDENT_KEY, User.Role.STUDENT)
        userRepository.save(student)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
    }


    def "three questions"() {
        //given a user with 3 submitted questions the returned data must be correctly sorted
        given: "three questionDto"
        def questionDto1 = new QuestionDto()
        questionDto1.setKey(1)
        questionDto1.setTitle(QUESTION_TITLE_1)
        questionDto1.setContent(QUESTION_CONTENT)
        questionDto1.setStatus(Question.Status.AVAILABLE.name())
        def questionDto2 = new QuestionDto()
        questionDto2.setKey(2)
        questionDto2.setTitle(QUESTION_TITLE_2)
        questionDto2.setContent(QUESTION_CONTENT)
        questionDto2.setStatus(Question.Status.AVAILABLE.name())
        def questionDto3 = new QuestionDto()
        questionDto3.setKey(3)
        questionDto3.setTitle(QUESTION_TITLE_3)
        questionDto3.setContent(QUESTION_CONTENT)
        questionDto3.setStatus(Question.Status.AVAILABLE.name())
        and: 'a optionId'
        def optionDto1 = new OptionDto()
        optionDto1.setContent(OPTION_CONTENT)
        optionDto1.setCorrect(true)
        def options1 = new ArrayList<OptionDto>()
        options1.add(optionDto1)
        questionDto1.setOptions(options1)
        def optionDto2 = new OptionDto()
        optionDto2.setContent(OPTION_CONTENT)
        optionDto2.setCorrect(true)
        def options2 = new ArrayList<OptionDto>()
        options2.add(optionDto2)
        questionDto2.setOptions(options2)
        def optionDto3 = new OptionDto()
        optionDto3.setContent(OPTION_CONTENT)
        optionDto3.setCorrect(true)
        def options3 = new ArrayList<OptionDto>()
        options3.add(optionDto3)
        questionDto3.setOptions(options3)

        questionService.submitQuestion(student.getId(), course.getId(), questionDto1)
        questionService.submitQuestion(student.getId(), course.getId(), questionDto2)
        questionService.submitQuestion(student.getId(), course.getId(), questionDto3)


        when:
        def sortedSubmittedQuestions = questionService.sortStudentSubmittedQuestionsByCreationDate(STUDENT_USERNAME1)

        then:
        sortedSubmittedQuestions[0].getTitle() == QUESTION_TITLE_3
        sortedSubmittedQuestions[1].getTitle() == QUESTION_TITLE_2
        sortedSubmittedQuestions[2].getTitle() == QUESTION_TITLE_1
    }

    def "no questions"() {
        //check if returned data from user with no submitted questions is correct
        given: "A user"
        def user = new User(STUDENT_NAME, STUDENT_USERNAME2, STUDENT_KEY + 1, User.Role.STUDENT)
        userRepository.save(user)

        when:
        questionService.sortStudentSubmittedQuestionsByCreationDate(STUDENT_USERNAME2)

        then: "the returned data are correct"
        def sortedSubmittedQuestions = user.getSubmittedQuestions()
        sortedSubmittedQuestions.size() == 0
    }

    @Unroll("invalid arguments: #username || errorMessage")
    def "invalid arguments"() {
        given: "an username"
        def userName = username

        when:
        questionService.sortStudentSubmittedQuestionsByCreationDate(userName)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        username || errorMessage
        null     || USERNAME_NOT_FOUND
        ""       || USERNAME_NOT_FOUND

    }

    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
}
