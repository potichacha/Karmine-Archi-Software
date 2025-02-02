package com.example.karmine.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class MessageQueue {

    @Id
    private String id; // Identifiant unique de la file d'attente

    @OneToMany(mappedBy = "queue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    public MessageQueue() {}

    public MessageQueue(String id) {
        this.id = id;
    }

    // Getters et setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        message.setQueue(this);
        this.messages.add(message);
    }

    public void removeMessage(Message message) {
        this.messages.remove(message);
        message.setQueue(null);
    }

    @Override
    public String toString() {
        return "MessageQueue{" +
                "id='" + id + '\'' +
                ", messages=" + messages.size() +
                '}';
    }
}
