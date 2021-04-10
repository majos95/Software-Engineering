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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class CreateTournamentServiceSpockTest extends Specification {

	static final String TOURNAMENT_NAME = "TournamentOne"
	static final String TOURNAMENT_NAME_1 = "Tournament1"
	static final Integer ONE_QUESTION = 1
	static final String TOPIC_NAME = "InterestingTopic"
	static final int USER_ID = 1
	static final String COURSE_NAME = "LEIC-T"
	static final String COURSE_EXECUTION_ACRONYM = "CS101"
	static final String COURSE_EXECUTION_ACADEMIC_TERM = "1º Semestre"
	static final LocalDateTime START_DATE = LocalDateTime.now()
	static final LocalDateTime END_DATE = START_DATE.plusDays(20)
	static final LocalDateTime OVERLAP_END_DATE = START_DATE
	static final LocalDateTime EARLY_END_DATE = START_DATE.minusDays(1)

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

	def tournament
	def tournamentDto
	def topic
	def topics
	def topicStandard
	def topicNameList
	def topicsEmpty
	def topicsNotEmpty
	def userStudent
	def course
	def courseExecution
	def formatter

	def setup() {
		formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

		course = new Course()
		course.setName(COURSE_NAME)
		courseRepository.save(course)

		topicStandard = new Topic()
		topicStandard.setName(TOPIC_NAME)
		topicStandard.setCourse(course)
		topicRepository.save(topicStandard)
		topic = new TopicDto(topicStandard)

		course.addTopic(topicStandard)
		courseRepository.save(course)

		topicNameList = new ArrayList<>()
		topicNameList.add(TOPIC_NAME)

		topicsEmpty = new HashSet<>()

		topicsNotEmpty = new HashSet<>()
		topicsNotEmpty.add(topic)

		topics = new HashSet<Topic>()
		topics.add(topicStandard)

		userStudent = new User()
		userStudent.setRole(User.Role.STUDENT)
		userStudent.setKey(USER_ID)
		userRepository.save(userStudent)


		courseExecution = new CourseExecution()
		courseExecution.setAcronym(COURSE_EXECUTION_ACRONYM)
		courseExecution.setAcademicTerm(COURSE_EXECUTION_ACADEMIC_TERM)
		courseExecution.setCourse(course)
		courseExecutionRepository.save(courseExecution)

		tournament = new Tournament()
		tournament.setName(TOURNAMENT_NAME)
		tournament.setStartDate(START_DATE)
		tournament.setEndDate(END_DATE)
		tournament.setTopics(topics)
		tournament.setNumQuestions(ONE_QUESTION)
		tournament.setCreator(userStudent)
		tournament.setCourseExecution(courseExecution)
		tournamentRepository.save(tournament)

		tournamentDto = new TournamentDto(tournament, true)
	}

	@Unroll
	def "invalid arguments: tournamentName=#tournamentName | startDate=#startDate | endDate=#endDate \
        | numQuestions=#numQuestions || errorMessage=#errorMessage"() {
		given: "a TournamentDto"
		def tournamentDto = new TournamentDto(tournament, true)
		tournamentDto.setName(tournamentName)
		tournamentDto.setStartDate(startDate ? startDate.format(formatter) : null)
		tournamentDto.setEndDate(endDate ? endDate.format(formatter) : null)
		tournamentDto.setNumQuestions(numQuestions)

		when:
		tournamentService.createTournament(userStudent.getId(), courseExecution.getId(), tournamentDto)

		then:
		def error = thrown(TutorException)
		error.errorMessage == errorMessage

		where:
		tournamentName  | startDate  | endDate          | numQuestions || errorMessage
		null            | START_DATE | END_DATE         | ONE_QUESTION || TOURNAMENT_NAME_EMPTY
		"  "            | START_DATE | END_DATE         | ONE_QUESTION || TOURNAMENT_NAME_EMPTY
		TOURNAMENT_NAME | null       | END_DATE         | ONE_QUESTION || TOURNAMENT_START_DATE_EMPTY
		TOURNAMENT_NAME | START_DATE | null             | ONE_QUESTION || TOURNAMENT_END_DATE_EMPTY
		TOURNAMENT_NAME | START_DATE | EARLY_END_DATE   | ONE_QUESTION || TOURNAMENT_INVALID_END_DATE
		TOURNAMENT_NAME | START_DATE | OVERLAP_END_DATE | ONE_QUESTION || TOURNAMENT_DATES_OVERLAP
		TOURNAMENT_NAME | START_DATE | END_DATE         | 0            || TOURNAMENT_NOT_ENOUGH_QUESTIONS
	}

	def "tournament creator is not a student"() {
		given: "a user that is not a student"
		userStudent.setRole(User.Role.TEACHER)

		when:
		tournamentService.createTournament((Integer) userStudent.getId(), (Integer) courseExecution.getId(), (TournamentDto) tournamentDto)

		then: "an exception is thrown"
		def exception = thrown(TutorException)
		exception.getErrorMessage() == TOURNAMENT_CREATOR_IS_NOT_STUDENT
	}

	def "create tournament without enough topics"() {
		given: "an empty list of topics"
		tournamentDto.setTopics(new HashSet<TopicDto>());

		when:
		tournamentService.createTournament(userStudent.getId(), courseExecution.getId(), tournamentDto)

		then: "an exception is thrown"
		def exception = thrown(TutorException)
		exception.getErrorMessage() == NOT_ENOUGH_TOPICS
	}

	def "all arguments are valid and create tournament"() {
		given: "a tournamentDto"
		def _tournament = new Tournament()
		_tournament.setName(TOURNAMENT_NAME_1)
		_tournament.setStartDate(START_DATE)
		_tournament.setEndDate(END_DATE)
		_tournament.setTopics(topics)
		_tournament.setNumQuestions(ONE_QUESTION)
		_tournament.setCreator(userStudent)
		_tournament.setCourseExecution(courseExecution)
		def tournamentDto = new TournamentDto(_tournament, true)

		when:
		def result = tournamentService.createTournament(userStudent.getId(), courseExecution.getId(), tournamentDto)

		then: "the returned data are correct"
		result.name == TOURNAMENT_NAME_1
		result.startDate == START_DATE.format(formatter)
		result.endDate == END_DATE.format(formatter)
		result.numQuestions == ONE_QUESTION
		result.topics.size() == 1
		and: "tournament is created"
		def tournament = tournamentRepository.findTournamentByName(courseExecution.getId(), TOURNAMENT_NAME_1).get()
		tournament != null
		and: "has the correct values"
		tournament.name == TOURNAMENT_NAME_1
		tournament.startDate.format(formatter) == START_DATE.format(formatter)
		tournament.endDate.format(formatter) == END_DATE.format(formatter)
		tournament.numQuestions == ONE_QUESTION
		tournament.topics.size() == 1
	}

	@TestConfiguration
	static class QuizServiceImplTestContextConfiguration {

		@Bean
		TournamentService tournamentService() {
			return new TournamentService()
		}
	}
}
