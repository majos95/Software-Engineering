package pt.ulisboa.tecnico.socialsoftware.tutor.doubt;

import org.hibernate.annotations.Type;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import java.util.HashMap;
import javax.persistence.*;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DOUBT_CONTENT_IS_EMPTY;

@Entity
@Table(name = "doubts")
public class Doubt {

    public enum Status {SOLVED, UNSOLVED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Status status = Status.UNSOLVED;

    private String content;

    @ManyToOne
    @JoinColumn(name = "discussion_id")
    private Discussion discussion;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "doubt")
    private Clarification clarification = null;

    @JoinColumn(name = "user_id")
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private User author;

    private String creationDate;


    @Column(name="is_new")
    @Type(type="true_false")
    private boolean isNew = false;

    public Doubt(){
    }

    public Doubt(User user, String creationDate, String content, boolean isNew, Discussion discussion){
        this.author = user;
        this.creationDate = creationDate;
        this.author.addDoubt(this);
        if (content == null || content.trim().isEmpty()) {
            throw new TutorException(DOUBT_CONTENT_IS_EMPTY);
        }
        this.content = content;
        this.isNew = isNew;
        this.discussion = discussion;
    }

    public Discussion getDiscussion() {
        return discussion;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public String getCreationDate() {
        return creationDate;
    }




    public void setContent(String content) {
        this.content = content;
    }



    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public Integer getId() {
        return id;
    }

    public Clarification getClarification() {
        return clarification;
    }

    public void setClarification(Clarification clarification) {
        this.clarification = clarification;
    }

    public void solve() {
        this.status = Status.SOLVED;
    }

    public void unsolve() {
        this.status = Status.UNSOLVED;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status){
        this.status = status;
    }


}

