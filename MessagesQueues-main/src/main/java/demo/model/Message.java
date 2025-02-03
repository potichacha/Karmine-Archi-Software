package demo.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime availableAt; // Quand ce message devient disponible
    private boolean isRead = false;

    @ManyToOne
    private MessageQueue queue; // Relation avec la file d’attente

    public Message() {}

    public Message(String content, long delayInSeconds) {
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.availableAt = this.createdAt.plusSeconds(delayInSeconds);
    }

    @ManyToMany(mappedBy = "messages", cascade = CascadeType.ALL)
    private List<Topic> topics;

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

    public MessageQueue getQueue() {
        return queue;
    }

    public void setQueue(MessageQueue queue) {
        this.queue = queue;
    }

    public boolean isRead() {
        return isRead;
    }

    public void removeMessage(Message message) {
        if (!message.isRead()) {
            throw new IllegalStateException("Message not read, cannot del");
        }
        this.messages ?.remove(message);
        message.setQueue(null);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", availableAt=" + availableAt +
                '}';
    }
}
