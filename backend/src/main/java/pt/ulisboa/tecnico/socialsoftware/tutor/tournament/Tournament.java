package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "tournaments")
public class Tournament {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User creator;

	@ManyToOne
	@JoinColumn(name = "course_execution_id")
	private CourseExecution courseExecution;

	@Column(name = "name")
	private String name;

	@Column(name = "start_date")
	private LocalDateTime startDate;

	@Column(name = "end_date")
	private LocalDateTime endDate;

	@ManyToMany
	@JoinTable(
			name = "topics_of_tournament",
			joinColumns = @JoinColumn(name = "tournament_id"),
			inverseJoinColumns = @JoinColumn(name = "topic_id")
	)
	private Set<Topic> topics;

	@Column(name = "num_questions")
	private Integer numQuestions;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "quiz_id")
	private Quiz quiz = null;

	@ManyToMany
	@JoinTable(
			name = "participants_of_tournament",
			joinColumns = @JoinColumn(name = "tournament_id"),
			inverseJoinColumns = @JoinColumn(name = "user_id")
	)
	private final Set<User> participants;

	@Enumerated(EnumType.STRING)
	private State state = State.CREATED;

	public enum State {
		CREATED, OPEN, CLOSED, CANCELLED
	}

	public Tournament() {
		this.participants = new HashSet<>();
		this.topics = new HashSet<>();
	}

	public Tournament(User creator, CourseExecution courseExecution) {
		this.participants = new HashSet<>();
		this.creator = creator;
		this.courseExecution = courseExecution;
		this.topics = new HashSet<>();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public void setCourseExecution(CourseExecution courseExecution) {
		this.courseExecution = courseExecution;
	}

	public CourseExecution getCourseExecution() {
		return courseExecution;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		checkName(name);
		this.name = name;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		checkStartDate(startDate);
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		checkEndDate(endDate);
		this.endDate = endDate;

		//scheduleEndTournament();
	}

	private void scheduleEndTournament() {
		ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

		Duration ms = Duration.between(LocalDateTime.now(), this.endDate);
		service.schedule(new TournamentStateTask(this, State.CLOSED), ms.toMillis(), TimeUnit.MILLISECONDS);
	}

	public Set<Topic> getTopics() {
		return topics;
	}

	public void setTopics(Set<Topic> topics) {
		this.topics = topics;
	}

	public void addTopic(Topic topic) {
		this.topics.add(topic);
	}

	public Integer getNumQuestions() {
		return numQuestions;
	}

	public void setNumQuestions(Integer numQuestions) {
		checkNumQuestions(numQuestions);
		this.numQuestions = numQuestions;
	}

	public Quiz getQuiz() {
		return quiz;
	}

	public void setQuiz(Quiz quiz) {
		this.quiz = quiz;
	}

	public void setState(State state) {
		this.state = state;
	}

	public State getState() {
		return state;
	}

	private void checkName(String name) {
		if (name == null || name.trim().length() == 0)
			throw new TutorException(TOURNAMENT_NAME_EMPTY);
	}

	private void checkStartDate(LocalDateTime startDate) {
		if (startDate == null)
			throw new TutorException(TOURNAMENT_START_DATE_EMPTY);
	}

	private void checkEndDate(LocalDateTime endDate) {
		if (this.startDate == null)
			throw new TutorException(TOURNAMENT_START_DATE_EMPTY);
		if (endDate == null)
			throw new TutorException(TOURNAMENT_END_DATE_EMPTY);
		if (endDate.isBefore(this.startDate))
			throw new TutorException(TOURNAMENT_INVALID_END_DATE);
		if (endDate.isEqual(this.startDate))
			throw new TutorException(TOURNAMENT_DATES_OVERLAP);
	}

	private void checkNumQuestions(Integer numQuestions) {
		if (numQuestions < 1)
			throw new TutorException(TOURNAMENT_NOT_ENOUGH_QUESTIONS);
	}

	public void addParticipant(User user) {
		if (participants.contains(user))
			throw new TutorException(STUDENT_ALREADY_ENROLLED);
		participants.add(user);
	}

	public Set<User> getParticipants() {
		return participants;
	}

	public void enrollStudent(User user) {
		switch (state) {
			case CREATED:
				throw new TutorException(INVALID_ENROLLMENT_CREATED_TOURNAMENT);
			case OPEN:
				addParticipant(user);
				break;
			case CLOSED:
				throw new TutorException(INVALID_ENROLLMENT_CLOSED_TOURNAMENT);
			case CANCELLED:
				throw new TutorException(INVALID_ENROLLMENT_CANCELLED_TOURNAMENT);
		}
	}

	public boolean isCreator(User user) {
		return this.creator.equals(user);
	}

	public void generateQuiz() {
		this.quiz = new Quiz();
		quiz.setType(Quiz.QuizType.PROPOSED.toString());
		quiz.setCourseExecution(this.courseExecution);
		quiz.setTitle(this.name + " Quiz");
		quiz.setScramble(true);
		quiz.setQrCodeOnly(false);
		quiz.setOneWay(false);
		quiz.setCreationDate(LocalDateTime.now());
		quiz.setAvailableDate(this.startDate);
		quiz.setConclusionDate(this.endDate);
		quiz.setResultsDate(this.endDate);
	}
}
