package demo.model;

import jakarta.persistence.*;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.ArrayList;
import java.util.List;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TopicMessage> topicMessages = new ArrayList<>();

    public Topic() {}

    public Topic(String name) {
        this.name = name;
    }

    // Getters & Setters
    public Long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<TopicMessage> getTopicMessages() { return topicMessages; }

    public void setTopicMessages(List<TopicMessage> topicMessages) {
        this.topicMessages = topicMessages;
    }

    public void addMessage(Message message) {
        int messageNumber = topicMessages.size() + 1;
        TopicMessage topicMessage = new TopicMessage(this, message, messageNumber);
        topicMessages.add(topicMessage);
        message.getTopics().add(this);
    }

    public void removeMessage(Message message) {
        topicMessages.removeIf(tm -> tm.getMessage().equals(message));
        message.getTopics().remove(this);
    }

    public List<Message> getMessages() {
        return topicMessages.stream()
                .map(TopicMessage::getMessage)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Topic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", messages=" + topicMessages.size() +
                '}';
    }
}