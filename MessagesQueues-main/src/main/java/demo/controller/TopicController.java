package demo.controller;

import demo.model.Message;
import demo.model.Topic;
import demo.service.TopicService;
import demo.service.QueueService;
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
    private final QueueService queueService;

    public TopicController(TopicService topicService, QueueService queueService) {
        this.topicService = topicService;
        this.queueService = queueService;
    }

    @PostConstruct
    public void init() {
        logger.info("✅ TopicController chargé par Spring Boot !");
    }

    // ✅ Création d'un Topic
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Topic> createTopic(@RequestBody Topic topic) {
        return ResponseEntity.ok(topicService.createTopic(topic));
    }

    // ✅ Récupérer tous les Topics
    @GetMapping
    public ResponseEntity<List<Topic>> getAllTopics() {
        return ResponseEntity.ok(topicService.getAllTopics());
    }

    // ✅ Ajouter un message à un Topic
    @PostMapping("/{topicId}/messages")
    public ResponseEntity<Topic> addMessageToTopic(@PathVariable Long topicId,
                                                   @RequestBody Message message) {
        Topic updatedTopic = topicService.addMessageToTopic(topicId, message);
        return ResponseEntity.ok(updatedTopic);
    }

    // ✅ Récupérer tous les messages d'un Topic
    @GetMapping("/{topicId}/messages")
    public ResponseEntity<List<Message>> getMessagesFromTopic(@PathVariable Long topicId) {
        return ResponseEntity.ok(topicService.getMessagesFromTopic(topicId));
    }

    // ✅ Supprimer un message d'un Topic
    @DeleteMapping("/{topicId}/messages/{messageId}")
    public ResponseEntity<Topic> removeMessageFromTopic(@PathVariable Long topicId,
                                                        @PathVariable Long messageId) {
        Topic updatedTopic = topicService.removeMessageFromTopic(topicId, messageId);
        return ResponseEntity.ok(updatedTopic);
    }

    // ✅ Récupérer le dernier message stocké dans la queue d'un Topic
    @GetMapping("/{topicId}/last-message")
    public ResponseEntity<Message> getLastMessage(@PathVariable Long topicId) {
        Topic topic = topicService.getAllTopics().stream()
                .filter(t -> t.getId().equals(topicId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("❌ Topic non trouvé : " + topicId));

        Message lastMessage = queueService.getLastMessage(topic);
        if (lastMessage == null) {
            return ResponseEntity.noContent().build(); // 204 No Content si aucun message
        }

        return ResponseEntity.ok(lastMessage);
    }
}