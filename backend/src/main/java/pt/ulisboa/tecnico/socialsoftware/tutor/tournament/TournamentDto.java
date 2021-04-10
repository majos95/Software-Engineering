package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.data.annotation.Transient;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TournamentDto implements Serializable {

	private Integer id;
	private Integer tournamentId;
	private UserDto creator;
	private Integer courseExecutionId;
	private String name;
	private String startDate;
	private String endDate;
	private Set<TopicDto> topics = new HashSet<>();
	private Integer numQuestions;
	private QuizDto quizDto = null;
	private Tournament.State state;

	@Transient
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public TournamentDto() {}


	public TournamentDto(Tournament tournament, boolean deepCopy) {
		this.id = tournament.getId();
		this.creator = new UserDto(tournament.getCreator());
		this.courseExecutionId = tournament.getCourseExecution().getId();
		this.name = tournament.getName();
		this.state = tournament.getState();

		if (tournament.getStartDate() != null)
			this.startDate = tournament.getStartDate().format(formatter);
		if (tournament.getEndDate() != null)
			this.endDate = tournament.getEndDate().format(formatter);

		this.numQuestions = tournament.getNumQuestions();

		if (deepCopy) {
			this.topics = tournament.getTopics().stream()
					.map(TopicDto::new)
					.collect(Collectors.toSet());
		}
		this.tournamentId = tournament.getId();
		if (tournament.getQuiz() != null)
			this.quizDto = new QuizDto(tournament.getQuiz(), true);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCourseExecutionId() {
		return courseExecutionId;
	}

	public void setCourseExecutionId(Integer courseExecution) {
		this.courseExecutionId = courseExecution;
	}

	public UserDto getCreator() {
		return creator;
	}

	public void setCreator(UserDto creator) {
		this.creator = creator;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Set<TopicDto> getTopics() {
		return topics;
	}

	public void setTopics(Set<TopicDto> topics) {
		this.topics = topics;
	}

	public int getNumQuestions() {
		return numQuestions;
	}

	public void setNumQuestions(int numQuestions) {
		this.numQuestions = numQuestions;
	}

	public QuizDto getQuizDto() {
		return quizDto;
	}

	public void setQuizDto(QuizDto quizDto) {
		this.quizDto = quizDto;
	}

	public Tournament.State getState() {
		return state;
	}

	public void setState(Tournament.State state) {
		this.state = state;
	}

	public LocalDateTime getStartDateDate() {
		if (getStartDate() == null || getStartDate().isEmpty()) {
			return null;
		}
		return LocalDateTime.parse(getStartDate(), formatter);
	}

	public LocalDateTime getEndDateDate() {
		if (getEndDate() == null || getEndDate().isEmpty()) {
			return null;
		}
		return LocalDateTime.parse(getEndDate(), formatter);
	}

	public Integer getTournamentId() {
		return tournamentId;
	}

	public void setTournamentId(Integer tournamentId) {
		this.tournamentId = tournamentId;
	}
}
