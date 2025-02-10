package demo.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.time.LocalDateTime;
<<<<<<< HEAD
=======
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
>>>>>>> fc2721e1074411741384b4178b1b0abd91c3495b

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

<<<<<<< HEAD
=======
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TopicMessage> topicMessages = new ArrayList<>();

>>>>>>> fc2721e1074411741384b4178b1b0abd91c3495b
    public Message() {}

    public Message(String content, long delayInSeconds) {
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.availableAt = this.createdAt.plusSeconds(delayInSeconds);
    }

<<<<<<< HEAD
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
=======
    // Getters & Setters
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

    public List<TopicMessage> getTopicMessages() { return topicMessages; }

    public void setTopicMessages(List<TopicMessage> topicMessages) {
        this.topicMessages = topicMessages;
    }

    /**
     * Ajoute ce message à un topic via l'entité intermédiaire TopicMessage.
     */
    public void addTopic(Topic topic) {
        TopicMessage topicMessage = new TopicMessage(topic, this, topic.getTopicMessages().size() + 1);
        topicMessages.add(topicMessage);
        topic.getTopicMessages().add(topicMessage);
    }

    /**
     * Supprime ce message d'un topic en retirant la relation TopicMessage.
     */
    public void removeTopic(Topic topic) {
        topicMessages.removeIf(tm -> tm.getTopic().equals(topic));
        topic.getTopicMessages().removeIf(tm -> tm.getMessage().equals(this));
    }

    @PreRemove
    private void checkIfInTopic() {
        if (topicMessages != null && !topicMessages.isEmpty()) {
            throw new IllegalStateException("Cannot delete message still used in topics");
        }
>>>>>>> fc2721e1074411741384b4178b1b0abd91c3495b
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
    public List<Topic> getTopics() {
        return topicMessages.stream()
                .map(TopicMessage::getTopic)
                .collect(Collectors.toList());
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