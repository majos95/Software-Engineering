package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class ShowOpenTournamentServiceSpockTest extends Specification {

	static final String TOURNAMENT_NAME_ONE = "Tournament_A"
	static final String TOURNAMENT_NAME_TWO = "Tournament_B"
	static final LocalDateTime START_DATE = LocalDateTime.now()
	static final LocalDateTime END_DATE = START_DATE.plusDays(20)
	static final Integer ONE_QUESTION = 1
	static final Integer USER_KEY = 1
	static final String COURSE_NAME = "LEIC-T"
	static final String COURSE_EXECUTION_ACRONYM = "CS101"
	static final String COURSE_EXECUTION_ACADEMIC_TERM = "1º Semestre"
	static final String TOPIC_NAME = "InterestingTopic"

	@Autowired
	TournamentService tournamentService

	@Autowired
	TournamentRepository tournamentRepository

	@Autowired
	CourseRepository courseRepository

	@Autowired
	CourseExecutionRepository courseExecutionRepository

	@Autowired
	UserRepository userRepository

	@Autowired
	TopicRepository topicRepository

	def courseExecution

	def setup() {
		courseExecution = new CourseExecution()
	}

	def "show tournaments associated with the user's course execution"() {
		given: "a course and a course executions"
		def course = new Course()
		course.setName(COURSE_NAME)
		courseRepository.save(course)
		courseExecution.setAcronym(COURSE_EXECUTION_ACRONYM)
		courseExecution.setAcademicTerm(COURSE_EXECUTION_ACADEMIC_TERM)
		courseExecution.setCourse(course)
		courseExecutionRepository.save(courseExecution)

		and: "a student"
		def student = new User()
		student.setRole(User.Role.STUDENT)
		student.setKey(USER_KEY)
		student.addCourse(courseExecution)
		userRepository.save(student)

		and: "a topic"
		def topic = new Topic()
		topic.setName(TOPIC_NAME)
		topicRepository.save(topic)

		and: "two tournaments"
		def tournamentOne = new Tournament()
		tournamentOne.setName(TOURNAMENT_NAME_TWO)
		tournamentOne.setStartDate(START_DATE)
		tournamentOne.setEndDate(END_DATE)
		tournamentOne.setNumQuestions(ONE_QUESTION)
		tournamentOne.setState(Tournament.State.OPEN)
		tournamentOne.setCourseExecution(courseExecution)
		tournamentOne.addTopic(topic)
		tournamentOne.setCreator(student)
		tournamentRepository.save(tournamentOne)
		tournamentService.enrollStudent(student.getId(), tournamentOne.getId())
		def tournamentTwo = new Tournament()
		tournamentTwo.setName(TOURNAMENT_NAME_ONE)
		tournamentTwo.setStartDate(START_DATE)
		tournamentTwo.setEndDate(END_DATE)
		tournamentTwo.setNumQuestions(ONE_QUESTION)
		tournamentTwo.setState(Tournament.State.OPEN)
		tournamentTwo.setCourseExecution(courseExecution)
		tournamentTwo.addTopic(topic)
		tournamentTwo.setCreator(student)
		tournamentRepository.save(tournamentTwo)
		tournamentService.enrollStudent(student.getId(), tournamentTwo.getId())

		when:
		def result = tournamentService.getTournaments(student.getId())

		then:
		def tournament_one = result.get(0)
		tournament_one.getName() == TOURNAMENT_NAME_ONE
		def tournament_two = result.get(1)
		tournament_two.getName() == TOURNAMENT_NAME_TWO
	}

	@TestConfiguration
	static class QuizServiceImplTestContextConfiguration {

		@Bean
		TournamentService tournamentService() {
			return new TournamentService()
		}
	}
}
