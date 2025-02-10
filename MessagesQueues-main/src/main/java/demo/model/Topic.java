package demo.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public void setTopicMessages(List<TopicMessage> topicMessages) { this.topicMessages = topicMessages; }

<<<<<<< HEAD
    /**
     * Ajoute un message à un topic.
=======
    public void setTopicMessages(List<TopicMessage> topicMessages) {
        this.topicMessages = topicMessages;
    }

    /**
     * Ajoute un message à ce topic en créant une relation TopicMessage.
>>>>>>> fc2721e1074411741384b4178b1b0abd91c3495b
     */
    public void addMessage(Message message) {
        int messageNumber = topicMessages.size() + 1;
        TopicMessage topicMessage = new TopicMessage(this, message, messageNumber);
        topicMessages.add(topicMessage);
<<<<<<< HEAD
    }

    /**
     * Supprime un message d'un topic.
     */
    public void removeMessage(Message message) {
        topicMessages.removeIf(tm -> tm.getMessage().equals(message));
    }

    /**
     * Récupère tous les messages associés à ce topic.
=======
        message.getTopicMessages().add(topicMessage); // Ajout aussi côté Message
    }

    /**
     * Supprime un message de ce topic en retirant la relation TopicMessage.
     */
    public void removeMessage(Message message) {
        topicMessages.removeIf(tm -> tm.getMessage().equals(message));
        message.getTopicMessages().removeIf(tm -> tm.getTopic().equals(this)); // Suppression aussi côté Message
    }

    /**
     * Retourne la liste des messages associés à ce topic.
>>>>>>> fc2721e1074411741384b4178b1b0abd91c3495b
     */
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