package demo.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime availableAt;
    private boolean isRead = false;

    @ManyToOne
    private MessageQueue queue;

    @ManyToMany(mappedBy = "messages", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<Topic> topics = new ArrayList<>();

    public Message() {}

    public Message(String content, long delayInSeconds) {
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.availableAt = this.createdAt.plusSeconds(delayInSeconds);
    }

    // Getters et setters
    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getAvailableAt() {
        return availableAt;
    }

    public void setAvailableAt(LocalDateTime availableAt) {
        this.availableAt = availableAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public MessageQueue getQueue() {
        return queue;
    }

    public void setQueue(MessageQueue queue) {
        this.queue = queue;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public void addTopic(Topic topic) {
        if (!this.topics.contains(topic)) {
            this.topics.add(topic);
            topic.getMessages().add(this);
        }
    }

    public void removeTopic(Topic topic) {
        if (this.topics.contains(topic)) {
            this.topics.remove(topic);
            topic.getMessages().remove(this);
        }
    }

    @PreRemove
    private void checkIfInTopic() {
        if (topics != null && !topics.isEmpty()) {
            throw new IllegalStateException("Cannot delete message still used in topics");
        }
    }

    public void removeFromQueue() {
        if (!isRead) {
            throw new IllegalStateException("Message not read, cannot delete");
        }
        if (this.queue != null) {
            this.queue.getMessages().remove(this);
        }
        this.queue = null;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", availableAt=" + availableAt +
                ", isRead=" + isRead +
                '}';
    }
}