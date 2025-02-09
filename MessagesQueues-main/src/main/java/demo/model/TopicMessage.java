package demo.model;

import jakarta.persistence.*;

@Entity
public class TopicMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;

    private int messageNumber; // Numérotation interne du message dans le topic

    public TopicMessage() {}

    public TopicMessage(Topic topic, Message message, int messageNumber) {
        this.topic = topic;
        this.message = message;
        this.messageNumber = messageNumber;
    }

    // Getters & Setters
    public Long getId() { return id; }

    public Topic getTopic() { return topic; }

    public void setTopic(Topic topic) { this.topic = topic; }

    public Message getMessage() { return message; }

    public void setMessage(Message message) { this.message = message; }

    public int getMessageNumber() { return messageNumber; }

    public void setMessageNumber(int messageNumber) { this.messageNumber = messageNumber; }
}