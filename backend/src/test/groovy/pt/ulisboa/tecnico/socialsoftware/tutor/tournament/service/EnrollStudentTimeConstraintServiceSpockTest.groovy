package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
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
class EnrollStudentTimeConstraintServiceSpockTest extends Specification {
    static final int USER_ID
    static final LocalDateTime USER_CREATION_DATE = LocalDateTime.now().withSecond(0).withNano(0)
    static final int TOURNAMENT_ID = 1
    static final String TOURNAMENT_NAME = "NewTournament"
    static final LocalDateTime START_DATE = LocalDateTime.now()
    static final LocalDateTime END_DATE = START_DATE.plusDays(20)
    static final int COURSE_EXECUTION_ID = 1
    static final Integer ONE_QUESTION = 1

    @Autowired
    TournamentService tournamentService

    @Autowired
    UserRepository userRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    def "a student tries to enroll in a closed tournament"() {
        given: "an user that is a student"
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
        tournament.setState(Tournament.State.CLOSED)
        tournamentRepository.save(tournament)

        when:
        tournamentService.enrollStudent(user.getId(), tournamentRepository.findAll().get(0).getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_ENROLLMENT_CLOSED_TOURNAMENT
    }

    def "a student tries to enroll in a tournament that hasn't started yet"() {
        given: "an user that is a student"
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
        tournament.setState(Tournament.State.CREATED)
        tournamentRepository.save(tournament)

        when:
        tournamentService.enrollStudent(user.getId(), tournamentRepository.findAll().get(0).getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_ENROLLMENT_CREATED_TOURNAMENT
    }

    def "a student tries to enroll in a tournament that is open"() {
        given: "an user that is a student"
        def courseExecution = new CourseExecution()
        courseExecutionRepository.save(courseExecution)
        Set<CourseExecution> courseExecutionSet = new HashSet<>();
        courseExecutionSet.add(courseExecution)
        def user = new User()
        user.setRole(User.Role.STUDENT)
        user.setKey(USER_ID)
        user.setCourseExecutions(courseExecutionSet)
        userRepository.save(user)
        and: "a tournament"
        def tournament = new Tournament()
        tournament.setName(TOURNAMENT_NAME)
        tournament.setStartDate(START_DATE)
        tournament.setEndDate(END_DATE)
        tournament.setNumQuestions(ONE_QUESTION)
        tournament.setState(Tournament.State.OPEN)
        tournament.setCourseExecution(courseExecution)
        def creator = new User()
        creator.setKey(1234)
        creator.setCourseExecutions(courseExecutionSet)
        userRepository.save(creator)
        tournament.setCreator(creator)
        tournamentRepository.save(tournament)

        when:
        tournamentService.enrollStudent(user.getId(), tournamentRepository.findAll().get(0).getId())

        then:
        tournament.getParticipants().contains(user) == true
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}