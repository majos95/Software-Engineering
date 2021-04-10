package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.Doubt;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;

@Entity
@Table(name = "clarifications")
public class Clarification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "doubt_id")
    private Doubt doubt;

    private String clarification;

    public Clarification() {}

    public Clarification(User author, Doubt doubt, ClarificationDto clarificationDto) {
        checkConsistentClarification(clarificationDto, author, doubt);
        this.author = author;
        this.doubt = doubt;
        this.clarification = clarificationDto.getDescription();
        doubt.setStatus(Doubt.Status.SOLVED);
        doubt.setClarification(this);
        //author.addClarification(this);

    }

    private void checkConsistentClarification(ClarificationDto clarificationDto, User author, Doubt doubt) {
        if(clarificationDto.getDescription() == null || clarificationDto.getDescription().trim().length() == 0) {
            throw new TutorException(ErrorMessage.CLARIFICATION_EMPTY);
        }

        if(author.getRole().compareTo(User.Role.TEACHER) != 0) {
            throw  new TutorException(ErrorMessage.CLARIFICATION_INVALID_USER);
        }

        if(doubt.getStatus().compareTo(Doubt.Status.UNSOLVED) != 0) {
            throw  new TutorException(ErrorMessage.CLARIFICATION_NOT_ALLOWED);
        }

        if(author.getCourseExecutions().stream()
                .noneMatch(courseExecution -> courseExecution.getCourse().equals(doubt.getDiscussion().getQuestion().getCourse()))) {
            throw new TutorException(ErrorMessage.CLARIFICATION_INVALID_COURSE_TEACHER);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Doubt getDoubt() {
        return doubt;
    }

    public void setDoubt(Doubt doubt) {
        this.doubt = doubt;
    }

    public String getClarification() {
        return clarification;
    }

    public void setClarification(String clarification) {
        this.clarification = clarification;
    }
}
