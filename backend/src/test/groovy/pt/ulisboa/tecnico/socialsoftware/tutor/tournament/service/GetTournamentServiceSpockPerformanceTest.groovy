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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

@DataJpaTest
class GetTournamentServiceSpockPerformanceTest extends Specification {

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

	@Autowired
	TopicRepository topicRepository

	def "get tournaments"() {
		given: "a user"
		def user = new User()
		user.setKey(1)
		userRepository.save(user)
		def userDto = new UserDto(user)
		and: "a whole lotta tournaments"
		1.upto(1, {
			def tournamentDto = new TournamentDto()
			tournamentDto.setName("LeTournament" + it)
			tournamentDto.setCreator(userDto)
		})

		when:
		1.upto(1, {
			def ret = tournamentService.getTournaments(userDto.getId())
		})

		then:
		true
	}

	@TestConfiguration
	static class ServiceImplTestContextConfiguration {

		@Bean
		TournamentService tournamentService() {
			return new TournamentService()
		}
	}
}