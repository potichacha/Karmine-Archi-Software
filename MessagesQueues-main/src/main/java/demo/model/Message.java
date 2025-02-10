package demo.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.time.LocalDateTime;

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

    public Message() {}

    public Message(String content, long delayInSeconds) {
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.availableAt = this.createdAt.plusSeconds(delayInSeconds);
    }

    // Getters et setters
    public Long getId() { return id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getAvailableAt() { return availableAt; }
    public void setAvailableAt(LocalDateTime availableAt) { this.availableAt = availableAt; }
    public boolean isRead() { return isRead; }
    public void markAsRead() { this.isRead = true; }
    public MessageQueue getQueue() { return queue; }
    public void setQueue(MessageQueue queue) { this.queue = queue; }

    @PreRemove
    private void checkIfInTopic() {
        throw new IllegalStateException("Cannot delete a message still used in topics.");
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