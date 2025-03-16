package demo.model;

import javax.persistence.*;
import demo.model.Topic;
import demo.model.Message;

@Entity
public class MessageQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Topic topic;

    @OneToOne
    private Message lastMessage;

    public MessageQueue() {}

    public MessageQueue(Topic topic, Message lastMessage) {
        this.topic = topic;
        this.lastMessage = lastMessage;
    }

    public Long getId() { return id; }
    public Topic getTopic() { return topic; }
    public Message getLastMessage() { return lastMessage; }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
}

