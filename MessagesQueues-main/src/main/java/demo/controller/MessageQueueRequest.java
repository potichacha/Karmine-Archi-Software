package com.example.karmine.controller;

public class MessageQueueRequest {
    private String content;
    private long delay;

    public MessageQueueRequest() {}

    public MessageQueueRequest(String content, long delay) {
        this.content = content;
        this.delay = delay;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }
}
