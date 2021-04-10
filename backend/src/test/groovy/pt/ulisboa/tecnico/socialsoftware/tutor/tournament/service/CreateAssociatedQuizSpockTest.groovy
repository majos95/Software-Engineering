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
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class CreateAssociatedQuizSpockTest extends Specification {

	static final String TOURNAMENT_NAME = "LeTournament"
	static final Integer CREATOR_KEY = 43000
	static final Integer OTHER_USER_KEY = 42000
	static final String TOPIC_NAME = "InterestingTopic"
	static final String COURSE_NAME = "Redis Engineering"
	static final String COURSE_EXECUTION_ACRONYM = "CS101"
	static final String COURSE_EXECUTION_ACADEMIC_TERM = "1º Semestre"
	static final LocalDateTime START_DATE = LocalDateTime.now().plusDays(1)
	static final LocalDateTime END_DATE = LocalDateTime.now().plusDays(2)
	static final Integer NUM_QUESTIONS = 6


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


	def tournamentDto
	def userCreator
	def otherUser
	def courseExecution


	def setup() {
		userCreator = new User()
		userCreator.setRole(User.Role.STUDENT)
		userCreator.setKey(CREATOR_KEY)
		userRepository.save(userCreator)

		otherUser = new User()
		otherUser.setRole(User.Role.STUDENT)
		otherUser.setKey(OTHER_USER_KEY)
		userRepository.save(otherUser)

		def course = new Course()
		course.setName(COURSE_NAME)
		courseRepository.save(course)

		def topic = new Topic()
		topic.setName(TOPIC_NAME)
		topic.setCourse(course)
		topicRepository.save(topic)

		def topicList = new HashSet();
		topicList.add(topic)

		courseExecution = new CourseExecution()
		courseExecution.setAcronym(COURSE_EXECUTION_ACRONYM)
		courseExecution.setAcademicTerm(COURSE_EXECUTION_ACADEMIC_TERM)
		courseExecution.setCourse(course)
		courseExecutionRepository.save(courseExecution)

		def tournament = new Tournament()
		tournament.setName(TOURNAMENT_NAME)
		tournament.setStartDate(START_DATE)
		tournament.setEndDate(END_DATE)
		tournament.setTopics(topicList)
		tournament.setNumQuestions(NUM_QUESTIONS)
		tournament.setCreator(userCreator)
		tournament.setCourseExecution(courseExecution)
		tournamentRepository.save(tournament)
		tournamentDto = new TournamentDto(tournament, true)
	}

	def "quiz not generated right after tournament creation"() {
		when: "a new tournament is created"
		def tournamentDto = tournamentService.createTournament(userCreator.getId(), courseExecution.getId(), tournamentDto)

		then: "the quiz has not been created"
		tournamentDto.getQuizDto() == null
	}

	def "quiz generated after tournament creation and another student enrolls"() {
		given: "a new tournament is created"
		def tournamentDto = tournamentService.createTournament(userCreator.getId(), courseExecution.getId(), tournamentDto)

		when: "another student enrolls"
		tournamentDto = tournamentService.enrollStudent(otherUser.getId(), tournamentDto.getId())

		then: "the quiz has been created"
		tournamentDto.getQuizDto() != null
	}


	@TestConfiguration
	static class ServiceImplTestContextConfiguration {

		@Bean
		TournamentService tournamentService() {
			return new TournamentService()
		}
	}
}
