package demo.model;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageQueue {
    private String id;
    private java.util.Queue<Message> messages;

    public MessageQueue() {
        this.messages = new ConcurrentLinkedQueue<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Queue<Message> getMessages() {
        return messages;
    }

    public void setMessages(Queue<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public Message nextMessage() {
        return this.messages.poll();
    }
}
