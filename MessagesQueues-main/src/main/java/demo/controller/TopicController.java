package com.example.controller;

import com.example.model.Message;
import com.example.model.Topic;
import com.example.service.TopicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/topics")
public class TopicController {

    private static final Logger logger = LoggerFactory.getLogger(TopicController.class);
    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PostConstruct
    public void init() {
        logger.info("✅ TopicController chargé par Spring Boot !");
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<Topic> createTopic(@RequestBody Topic topic) {
        return ResponseEntity.ok(topicService.createTopic(topic));
    }

    @GetMapping
    public ResponseEntity<List<Topic>> getAllTopics() {
        return ResponseEntity.ok(topicService.getAllTopics());
    }

    // 🚀 Ajoute un message à un Topic
    @PostMapping("/{topicId}/messages")
    public ResponseEntity<Topic> addMessageToTopic(@PathVariable Long topicId,
                                                   @RequestBody Message message) {
        Topic updatedTopic = topicService.addMessageToTopic(topicId, message);
        return ResponseEntity.ok(updatedTopic);
    }

    // 🚀 Récupère tous les messages d'un Topic
    @GetMapping("/{topicId}/messages")
    public ResponseEntity<List<Message>> getMessagesFromTopic(@PathVariable Long topicId) {
        return ResponseEntity.ok(topicService.getMessagesFromTopic(topicId));
    }

    @DeleteMapping("/{topicId}/messages/{messageId}")
    public ResponseEntity<Topic> removeMessageFromTopic(@PathVariable Long topicId,
                                                        @PathVariable Long messageId) {
        Topic updatedTopic = topicService.removeMessageFromTopic(topicId, messageId);
        return ResponseEntity.ok(updatedTopic);
    }
}