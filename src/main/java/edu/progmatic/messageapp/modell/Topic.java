package edu.progmatic.messageapp.modell;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    private RegisteredUser author;
    @OneToMany(mappedBy="topic")
    private List<Message> topicMessages = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RegisteredUser getAuthor() {
        return author;
    }

    public void setAuthor(RegisteredUser author) {
        this.author = author;
    }

    public List<Message> getTopicMessages() {
        return topicMessages;
    }

    public void setTopicMessages(List<Message> topicMessages) {
        this.topicMessages = topicMessages;
    }
}
