package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class EnrollStudentServiceSpockTest extends Specification {

    static final int USER_ID
    static final String USER_NAME = "UserName"
    static final String USER_NICKNAME = "UserNickname"
    static final LocalDateTime USER_CREATION_DATE = LocalDateTime.now().withSecond(0).withNano(0)
    static Tournament tournament = new Tournament()
    static final int TOURNAMENT_ID = 1
    static final String TOURNAMENT_NAME = "NewTournament"
    static final LocalDateTime START_DATE = LocalDateTime.now()
    static final LocalDateTime END_DATE = START_DATE.plusDays(20)
    static final int COURSE_EXECUTION_ID = 1
    static final Integer ONE_QUESTION = 1
    static closedTournamentState = Tournament.State.CLOSED
    static createdTournamentState = Tournament.State.CREATED
    static cancelledTournamentState = Tournament.State.CANCELLED
    static final String COURSE_NAME = "Course"

    static final user = new User()

    def courseExecution
    def formatter

    @Autowired
    TournamentService tournamentService

    @Autowired
    UserRepository userRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    def setup() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        courseExecution = new CourseExecution()

        tournament.setState(Tournament.State.OPEN)
        tournament.setStartDate(START_DATE)
        tournament.setEndDate(END_DATE)
        tournament.setCreator(user)
        tournament.setCourseExecution(courseExecution)
        tournament.setNumQuestions(1)

    }


    def "a user that isn't a student tries to enroll"() {
        given: "a user that isn't a student"
        def course = new Course()
        course.setName(COURSE_NAME)
        courseRepository.save(course)
        def courseExecution = new CourseExecution()
        courseExecution.setCourse(course)
        courseExecutionRepository.save(courseExecution)
        user.setRole(User.Role.TEACHER)
        user.setKey(USER_ID)
        userRepository.save(user)
        and: "a tournament"
        def tournament = new Tournament(user, courseExecution)
        tournament.setName(TOURNAMENT_NAME)
        tournament.setStartDate(START_DATE)
        tournament.setEndDate(END_DATE)
        tournament.setNumQuestions(ONE_QUESTION)
        tournament.setCourseExecution(courseExecution)
        tournamentRepository.save(tournament)


        when:
        tournamentService.enrollStudent(user.getId(), tournament.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_ENROLLMENT_ATTEMPT_NOT_STUDENT
    }

    def "the student is already enrolled in the tournament"() {
        given: "an user that is a student"
        def course = new Course()
        course.setName(COURSE_NAME)
        courseRepository.save(course)
        def courseExecution = new CourseExecution()
        courseExecution.setCourse(course)
        courseExecutionRepository.save(courseExecution)
        def user = new User()
        user.setRole(User.Role.STUDENT)
        user.setKey(USER_ID)
        userRepository.save(user)
        and: "a tournament"
        def tournament = new Tournament()
        tournament.setName(TOURNAMENT_NAME)
        tournament.setStartDate(START_DATE)
        tournament.setEndDate(END_DATE)
        tournament.setNumQuestions(ONE_QUESTION)
        tournament.setState(Tournament.State.OPEN)
        tournament.setCreator(user)
        tournament.setCourseExecution(courseExecution)
        tournamentRepository.save(tournament)

        when:
        tournamentService.enrollStudent(user.getId(), tournamentRepository.findAll().get(0).getId())
        tournamentService.enrollStudent(user.getId(), tournamentRepository.findAll().get(0).getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.STUDENT_ALREADY_ENROLLED
    }

    @Unroll
    def "invalid arguments where student is #studentExists and tournament is #tournamentExists and errorMessage is #errorMessage"() {
        when:
        tournamentService.enrollStudent(studentExists, tournamentExists)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        studentExists          |   tournamentExists    ||  errorMessage
        null                   |   TOURNAMENT_ID       ||  INVALID_USER_ID
        USER_ID                |   null                ||  INVALID_TOURNAMENT_ID
    }

    @Unroll
    def "invalid enrollment attempt where the student exists and the tournament's state is #state and errorMessage is #errorMessage"() {
        given: "a student"
        def student = new User()
        student.setRole(User.Role.STUDENT)
        student.setKey(USER_ID)
        userRepository.save(student)
        and: "a tournament"
        def tournament = new Tournament()
        tournament.setName(TOURNAMENT_NAME)
        tournament.setStartDate(START_DATE)
        tournament.setEndDate(END_DATE)
        tournament.setNumQuestions(ONE_QUESTION)
        tournament.setState(state)

        when:
        tournamentRepository.save(tournament)
        tournamentService.enrollStudent(student.getId(), tournament.getId())

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        state                         ||  errorMessage
        closedTournamentState         ||  INVALID_ENROLLMENT_CLOSED_TOURNAMENT
        createdTournamentState        ||  INVALID_ENROLLMENT_CREATED_TOURNAMENT
        cancelledTournamentState      ||  INVALID_ENROLLMENT_CANCELLED_TOURNAMENT
    }

    def "valid enrollment where the student exists and the tournament is open"() {
        given: "a student"
        def user = new User()
        user.setRole(User.Role.STUDENT)
        user.setKey(USER_ID)
        user.setName(USER_NAME)
        user.setUsername(USER_NICKNAME)
        user.setCreationDate(USER_CREATION_DATE)
        def courseExecutions = new HashSet<CourseExecution>()
        courseExecutions.add(courseExecution)
        user.setCourseExecutions(courseExecutions)
        userRepository.save(user)
        and: "a tournament"
        def tournament = new Tournament()
        tournament.setName(TOURNAMENT_NAME)
        tournament.setStartDate(START_DATE)
        tournament.setEndDate(END_DATE)
        tournament.setNumQuestions(ONE_QUESTION)
        tournament.setState(Tournament.State.OPEN)
        tournament.setCreator(user)
        tournament.setCourseExecution(courseExecution)
        tournamentRepository.save(tournament)

        when:
        def newUser = new User()
        newUser.setRole(User.Role.STUDENT)
        newUser.setKey(USER_ID + 1)
        newUser.setCourseExecutions(courseExecutions)
        userRepository.save(newUser)
        tournamentService.enrollStudent(newUser.getId(), tournament.getId())

        then:
        tournament.getParticipants().contains(newUser) == true
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}