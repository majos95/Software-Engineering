package pt.ulisboa.tecnico.socialsoftware.tutor.doubt;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.doubt.Doubt;

import java.io.Serializable;

public class DoubtDto implements Serializable {
    private Integer id;
    private String content;
    private String author;
    private Doubt.Status status;
    private ClarificationDto clarificationDto;
    private String title;
    private String creationDate;
    private boolean isNew;

    public DoubtDto(){
    }

    public DoubtDto(Doubt doubt){
        this.id = doubt.getId();
        this.content = doubt.getContent();
        this.author = doubt.getAuthor().getName();
        this.creationDate = doubt.getCreationDate();
        this.status = doubt.getStatus();
        if(doubt.getClarification() != null) {
            this.clarificationDto = new ClarificationDto(doubt.getClarification());
        }
        this.isNew = doubt.isNew();
    }

    public String getCreationDate() {
        return creationDate;
    }

    public boolean isNew(){
        return isNew;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ClarificationDto getClarificationDto() {
        return clarificationDto;
    }

    public void setClarificationDto(ClarificationDto clarificationDto) {
        this.clarificationDto = clarificationDto;
    }

    public Doubt.Status getStatus() {
        return status;
    }

    public void setStatus(Doubt.Status status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
