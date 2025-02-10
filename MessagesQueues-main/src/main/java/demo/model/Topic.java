package demo.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.HashSet;
import java.util.Set;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(
            name = "topic_message",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "message_id")
    )
    private Set<Message> messages = new HashSet<>();

    public Topic() {}

    public Topic(String name) {
        this.name = name;
    }

    // Getters & Setters
    public Long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Set<Message> getMessages() { return messages; }

    public void setMessages(Set<Message> messages) { this.messages = messages; }

    public void addMessage(Message message) {
        if (!this.messages.contains(message)) {
            this.messages.add(message);
            message.getTopics().add(this);
        }
    }

    public void removeMessage(Message message) {
        this.messages.remove(message);
        message.getTopics().remove(this);
    }

    @Override
    public String toString() {
        return "Topic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", messages=" + messages.size() +
                '}';
    }
}