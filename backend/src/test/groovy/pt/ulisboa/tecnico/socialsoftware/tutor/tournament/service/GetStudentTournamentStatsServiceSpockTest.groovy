package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowire
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class GetStudentTournamentStatsServiceSpockTest extends Specification {

	static final String TOURNAMENT_NAME = "NewTournament"
	static final LocalDateTime START_DATE = LocalDateTime.now()
	static final LocalDateTime END_DATE = START_DATE.plusDays(20)
	static final Integer ONE_QUESTION = 1
	static final String COURSE_NAME = "Course"
	static final String TOPIC_NAME = "Topic"

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

	@Autowired
	TopicRepository topicRepository


	def setup() {
	}

	def "get student stats with no recorded stats"() {
		given: "a student"
		def user = new User()
		user.setKey(1)
		userRepository.save(user)

		when:
		def result = tournamentService.getStudentTournamentStats(user.getId())

		then:
		result.getAmountOfParticipatedTournaments() == 0
	}

	def "get student stats with recorded stats"() {
		given: "a student"
		def user = new User()
		user.setRole(User.Role.STUDENT)
		user.setKey(1)
		userRepository.save(user)
		and: "a course execution"
		def course = new Course()
		course.setName(COURSE_NAME)
		courseRepository.save(course)
		def courseExecution = new CourseExecution()
		courseExecution.setCourse(course)
		courseExecutionRepository.save(courseExecution)
		and: "a topic"
		def topic = new Topic()
		topic.setName(TOPIC_NAME)
		topic.setCourse(course)
		topicRepository.save(topic)
		and: "a tournament"
		def tournament = new Tournament(user, courseExecution)
		tournament.setName(TOURNAMENT_NAME)
		tournament.setStartDate(START_DATE)
		tournament.setEndDate(END_DATE)
		tournament.setNumQuestions(ONE_QUESTION)
		tournament.setCourseExecution(courseExecution)
		tournament.setState(Tournament.State.OPEN)
		tournament.addTopic(topic)
		tournamentRepository.save(tournament)

		when:
		tournamentService.enrollStudent(user.getId(), tournament.getId())
		def result = tournamentService.getStudentTournamentStats(user.getId())

		then:
		result.getAmountOfParticipatedTournaments() == 1
	}

	@TestConfiguration
	static class ServiceImplTestContextConfiguration {

		@Bean
		TournamentService tournamentService() {
			return new TournamentService()
		}
	}
}
