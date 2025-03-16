package demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private LocalDateTime timeCreated;
    private LocalDateTime timeFirstAccessed;
    private int numberOfReads;

    @ManyToMany(mappedBy = "messages")
    @JsonIgnoreProperties("messages") // 🚀 Remplace @JsonBackReference
    private List<Topic> topics = new ArrayList<>();

    public Message() {
        this.timeCreated = LocalDateTime.now();
        this.numberOfReads = 0;
    }

    public Long getId() { return id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimeCreated() { return timeCreated; }
    public void setTimeCreated(LocalDateTime timeCreated) { this.timeCreated = timeCreated; }

    public LocalDateTime getTimeFirstAccessed() { return timeFirstAccessed; }
    public void setTimeFirstAccessed(LocalDateTime timeFirstAccessed) { this.timeFirstAccessed = timeFirstAccessed; }

    public int getNumberOfReads() { return numberOfReads; }
    public void setNumberOfReads(int numberOfReads) { this.numberOfReads = numberOfReads; }

    public List<Topic> getTopics() { return topics; }
    public void setTopics(List<Topic> topics) { this.topics = topics; }
}