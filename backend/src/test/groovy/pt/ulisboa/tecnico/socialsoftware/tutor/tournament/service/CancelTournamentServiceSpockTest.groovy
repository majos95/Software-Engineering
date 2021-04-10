package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class CancelTournamentServiceSpockTest extends Specification {

	static final int VALID_TOURNAMENT_CREATOR = 42000
	static final int VALID_USER = 43000
	static final int INVALID_USER = 100000
	static final int VALID_TOURNAMENT = 1337
	static final int INVALID_TOURNAMENT = 100000
	static final String TOURNAMENT_NAME = "LeTournament"
	static final String COURSE_NAME = "LEIC-T"
	static final String COURSE_EXECUTION_ACRONYM = "CS101"
	static final String COURSE_EXECUTION_ACADEMIC_TERM = "1º Semestre"
	static final LocalDateTime START_DATE = LocalDateTime.now().plusDays(1)
	static final LocalDateTime END_DATE = LocalDateTime.now().plusDays(2)


	@Autowired
	TournamentService tournamentService

	@Autowired
	CourseExecutionRepository courseExecutionRepository

	@Autowired
	CourseRepository courseRepository

	@Autowired
	UserRepository userRepository

	@Autowired
	TopicRepository topicRepository

	@Autowired
	TournamentRepository tournamentRepository


	def creatorStudent
	def userStudent
	def course
	def courseExecution
	def tournament

	def setup() {
		creatorStudent = new User()
		creatorStudent.setRole(User.Role.STUDENT)
		creatorStudent.setKey(VALID_TOURNAMENT_CREATOR)
		userRepository.save(creatorStudent)

		userStudent = new User()
		userStudent.setRole(User.Role.STUDENT)
		userStudent.setKey(VALID_USER)
		userRepository.save(userStudent)

		course = new Course()
		course.setName(COURSE_NAME)
		courseRepository.save(course)

		courseExecution = new CourseExecution()
		courseExecution.setAcronym(COURSE_EXECUTION_ACRONYM)
		courseExecution.setAcademicTerm(COURSE_EXECUTION_ACADEMIC_TERM)
		courseExecution.setCourse(course)
		courseExecutionRepository.save(courseExecution)

		tournament = new Tournament()
		tournament.setCourseExecution(courseExecution)
		tournament.setCreator(creatorStudent)
		tournament.setName(TOURNAMENT_NAME)
		tournament.setStartDate(START_DATE)
		tournament.setEndDate(END_DATE)
		tournamentRepository.save(tournament)
	}

	@Unroll
	def "invalid arguments: userId=#userId | tournamentId=#tournamentId \
			|| errorMessage=#errorMessage"() {
		when:
		tournamentService.cancelTournament(userId, tournamentId)

		then:
		def error = thrown(TutorException)
		error.errorMessage == errorMessage

		where:
		userId | tournamentId || errorMessage
		null   | 1            || INVALID_USER_ID
		1      | null         || INVALID_TOURNAMENT_ID
	}

	def "tournament doesn't exist"() {
		when:
		tournamentService.cancelTournament(userStudent.getId(), INVALID_TOURNAMENT)

		then: "an exception is thrown"
		def exception = thrown(TutorException)
		exception.getErrorMessage() == TOURNAMENT_NOT_FOUND
	}

	def "user doesn't exist"() {
		when:
		tournamentService.cancelTournament(INVALID_USER, VALID_TOURNAMENT)

		then: "an exception is thrown"
		def exception = thrown(TutorException)
		exception.getErrorMessage() == USER_NOT_FOUND
	}

	def "user that attempts tournament cancellation isn't the creator"() {
		when:
		tournamentService.cancelTournament(userStudent.getId(), tournament.getId())

		then: "an exception is thrown"
		def exception = thrown(TutorException)
		exception.getErrorMessage() == USER_NOT_TOURNAMENT_CREATOR
	}

	def "valid user attempts to cancel a closed tournament"() {
		given:
		tournament.setState(Tournament.State.CLOSED)

		when:
		tournamentService.cancelTournament(creatorStudent.getId(), tournament.getId())

		then: "an exception is thrown"
		def exception = thrown(TutorException)
		exception.getErrorMessage() == CANNOT_CANCEL_CLOSED_TOURNAMENT
		and: "set the state back to created"
		tournament.setState(Tournament.State.CREATED)
	}

	def "valid user attempts to cancel a cancelled tournament"() {
		given:
		tournament.setState(Tournament.State.CANCELLED)

		when:
		tournamentService.cancelTournament(creatorStudent.getId(), tournament.getId())

		then: "an exception is thrown"
		def exception = thrown(TutorException)
		exception.getErrorMessage() == CANNOT_CANCEL_CANCELLED_TOURNAMENT
		and: "set the state back to created"
		tournament.setState(Tournament.State.CREATED)
	}

	def "successful tournament cancellation"() {
		when:
		tournamentService.cancelTournament(creatorStudent.getId(), tournament.getId())

		then: "the tournament state is cancelled"
		def tournament = tournamentRepository.findTournamentByName(courseExecution.getId(), TOURNAMENT_NAME).get()
		tournament != null
		and: "values are correct"
		tournament.name == TOURNAMENT_NAME
		tournament.state == Tournament.State.CANCELLED
	}


	@TestConfiguration
	static class ServiceImplTestContextConfiguration {

		@Bean
		TournamentService tournamentService() {
			return new TournamentService()
		}
	}
}
