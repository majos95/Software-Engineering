package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class TournamentService {

	@Autowired
	private CourseExecutionRepository courseExecutionRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private QuizRepository quizRepository;

	@Autowired
	private TournamentRepository tournamentRepository;

	@Autowired
	private TopicRepository topicRepository;

	@PersistenceContext
	EntityManager entityManager;

	public TournamentService() {}

	@Retryable(
			value = { SQLException.class },
			backoff = @Backoff(delay = 5000))
	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public TournamentDto createTournament(int userId, int courseExecutionId, TournamentDto tournamentDto) {
		CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));

		User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

		Set<TopicDto> topicDtos = tournamentDto.getTopics();

		if (topicDtos.isEmpty())
			throw new TutorException(NOT_ENOUGH_TOPICS);

		if (tournamentDto.getStartDate() == null)
			throw new TutorException(TOURNAMENT_START_DATE_EMPTY);

		if (tournamentDto.getEndDate() == null)
			throw new TutorException(TOURNAMENT_END_DATE_EMPTY);

		Set<Topic> topics = new HashSet<>();
		for (TopicDto topicDto : topicDtos) {
			Topic topic = topicRepository.findTopicByName(courseExecution.getCourse().getId(), topicDto.getName());
			if (topic == null)
				throw new TutorException(TOPIC_WITH_NAME_NOT_FOUND, topicDto.getName());

			topics.add(topic);
		}

		if (user.getRole() != User.Role.STUDENT)
			throw new TutorException(TOURNAMENT_CREATOR_IS_NOT_STUDENT);

		Tournament tournament = new Tournament(user, courseExecution);

		tournament.setName(tournamentDto.getName());
		tournament.setStartDate(LocalDateTime.parse(tournamentDto.getStartDate() , DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		tournament.setEndDate(LocalDateTime.parse(tournamentDto.getEndDate() , DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		tournament.setNumQuestions(tournamentDto.getNumQuestions());
		tournament.setTopics(topics);
		tournament.setState(Tournament.State.OPEN);
		tournament.enrollStudent(user);

		tournamentRepository.save(tournament);

		user.addTournament(tournament);
		userRepository.save(user);

		return new TournamentDto(tournament, true);
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public TournamentDto enrollStudent(Integer userId, Integer tournamentId) {
		if (userId == null)
			throw new TutorException(INVALID_USER_ID);
		else if (tournamentId == null)
			throw new TutorException(INVALID_TOURNAMENT_ID);

		User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

		if (user.getRole() != User.Role.STUDENT)
			throw new TutorException(INVALID_ENROLLMENT_ATTEMPT_NOT_STUDENT);

		Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));

		if (Duration.between(LocalDateTime.now(), tournament.getEndDate()).toMillis() < 0 && tournament.getState() != Tournament.State.CLOSED) {
			tournament.setState(Tournament.State.CLOSED);
		}

		tournament.enrollStudent(user);

		if (tournament.getParticipants().size() > 1) {
			tournament.generateQuiz();
			Quiz quiz = tournament.getQuiz();
			quizRepository.save(quiz);
		}

		tournamentRepository.save(tournament);

		user.addTournament(tournament);
		userRepository.save(user);

		return new TournamentDto(tournament, true);
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public List<TournamentDto> getTournaments(Integer userId) {
		if (userId == null)
			throw new TutorException(INVALID_USER_ID);

		User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

		List<Tournament> tournaments = new ArrayList<>();

		for (CourseExecution courseExecution : user.getCourseExecutions())
			tournaments.addAll(tournamentRepository.findTournaments(courseExecution.getId()));

		List<TournamentDto> tournamentsDto = new ArrayList<>();

		for (Tournament tournament: tournaments)
			tournamentsDto.add(new TournamentDto(tournament, true));

		return tournamentsDto.stream()
				.sorted(Comparator.comparing(TournamentDto::getName))
				.collect(Collectors.toList());
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public TournamentDto cancelTournament(Integer userId, Integer tournamentId) {
		if (userId == null)
			throw new TutorException(INVALID_USER_ID);
		else if (tournamentId == null)
			throw new TutorException(INVALID_TOURNAMENT_ID);

		User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
		Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));

		if (!tournament.isCreator(user))
			throw new TutorException(USER_NOT_TOURNAMENT_CREATOR, user.getId());

		if (tournament.getState().equals(Tournament.State.CLOSED))
			throw new TutorException(CANNOT_CANCEL_CLOSED_TOURNAMENT);
		else if (tournament.getState().equals(Tournament.State.CANCELLED))
			throw new TutorException(CANNOT_CANCEL_CANCELLED_TOURNAMENT);

		tournament.setState(Tournament.State.CANCELLED);

		tournamentRepository.save(tournament);

		return new TournamentDto(tournament, true);
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public StudentDto getStudentTournamentStats(Integer userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

		return new StudentDto(user);
	}
}
