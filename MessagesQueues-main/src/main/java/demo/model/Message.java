package com.example.karmine.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime availableAt; // Quand ce message devient disponible

    @ManyToOne
    private MessageQueue queue; // Relation avec la file d’attente

    public Message() {}

    public Message(String content, long delayInSeconds) {
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.availableAt = this.createdAt.plusSeconds(delayInSeconds);
    }

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
