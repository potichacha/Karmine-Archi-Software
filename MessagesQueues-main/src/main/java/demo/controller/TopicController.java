package demo.controller;

import com.mysql.cj.log.Log;
import demo.model.Message;
import demo.model.Topic;
import demo.service.LogWorkerService;
import demo.service.TopicService;
import demo.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/topics")
public class TopicController {

    private static final Logger logger = LoggerFactory.getLogger(TopicController.class);
    private final TopicService topicService;
    private final QueueService queueService;
    private final LogWorkerService logWorkerService;

    @Autowired
    public TopicController(TopicService topicService, QueueService queueService, LogWorkerService logWorkerService) {
        this.topicService = topicService;
        this.queueService = queueService;
        this.logWorkerService = logWorkerService;
    }

    @PostConstruct
    public void init() {
        logger.info("✅ TopicController chargé par Spring Boot !");
        try {
            if (topicService.findTopicByName("logs") == null) {
                topicService.createTopic(new Topic("logs"));
                logger.info("✅ Topic 'logs' créé !");
            }
        }
        catch (DataIntegrityViolationException e) {
            logger.warn("Topic 'logs' already exists. Ignoring creation.");
        }
    }

    // ✅ Création d'un Topic
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Topic> createTopic(@RequestBody Topic topic) {
        Topic createdTopic = topicService.createTopic(topic);
        logger.info("✅ Nouveau topic créé : {}", createdTopic.getName());
        logWorkerService.sendLog("New topic created: " + topic, "newtopic");
        return ResponseEntity.ok(createdTopic);
    }

    // ✅ Récupérer tous les Topics
    @GetMapping
    public ResponseEntity<List<Topic>> getAllTopics() {
        List<Topic> topics = topicService.getAllTopics();
        logger.info("✅ {} topics récupérés.", topics.size());
        return ResponseEntity.ok(topics);
    }

    // ✅ Récupérer un topic par son ID
    @GetMapping("/{topicId}")
    public ResponseEntity<Topic> getTopicById(@PathVariable Long topicId) {
        return topicService.getTopicById(topicId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("❌ Topic {} non trouvé.", topicId);
                    return ResponseEntity.notFound().build();
                });
    }

    // ✅ Ajouter un message à un Topic
    @PostMapping("/{topicId}/messages")
    public ResponseEntity<Topic> addMessageToTopic(@PathVariable Long topicId, @RequestBody Message message) {
        Topic updatedTopic = topicService.addMessageToTopic(topicId, message);
        logger.info("✅ Message ajouté au topic {}.", topicId);
        logWorkerService.sendLog("Message added to topic " + topicId + ": " + message, "addmessage");
        return ResponseEntity.ok(updatedTopic);
    }

    // ✅ Récupérer tous les messages d'un Topic
    @GetMapping("/{topicId}/messages")
    public ResponseEntity<List<Message>> getMessagesFromTopic(@PathVariable Long topicId) {
        List<Message> messages = topicService.getMessagesFromTopic(topicId);
        logger.info("✅ {} messages récupérés pour le topic {}.", messages.size(), topicId);
        return ResponseEntity.ok(messages);
    }

    // ✅ Supprimer un message d'un Topic
    @DeleteMapping("/{topicId}/messages/{messageId}")
    public ResponseEntity<Topic> removeMessageFromTopic(@PathVariable Long topicId, @PathVariable Long messageId) {
        Topic updatedTopic = topicService.removeMessageFromTopic(topicId, messageId);
        logger.info("✅ Message {} supprimé du topic {}.", messageId, topicId);
        logWorkerService.sendLog("Message " + messageId + " deleted from topic " + topicId, "deletemessage");
        return ResponseEntity.ok(updatedTopic);
    }

    // ✅ Récupérer le dernier message stocké dans la queue d'un Topic
    @GetMapping("/{topicId}/last-message")
    public ResponseEntity<Message> getLastMessage(@PathVariable Long topicId) {
        Topic topic = topicService.getTopicById(topicId)
                .orElseThrow(() -> new RuntimeException("❌ Topic non trouvé : " + topicId));

        Message lastMessage = queueService.getLastMessage(topic);
        if (lastMessage == null) {
            logger.info("✅ Aucun message dans la queue pour le topic {}.", topicId);
            return ResponseEntity.noContent().build();
        }

        logger.info("✅ Dernier message récupéré pour le topic {}.", topicId);
        return ResponseEntity.ok(lastMessage);
    }

    @DeleteMapping("/{topicId}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long topicId) {
        Optional<Topic> topic = topicService.getTopicById(topicId);
        if (topic.isPresent()) {
            topicService.deleteTopic(topicId);
            logger.info("✅ Topic {} supprimé.", topicId);
            logWorkerService.sendLog("Topic deleted: " + topicId, "deletetopic");
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            logger.warn("❌ Tentative de suppression échouée, topic {} non trouvé.", topicId);
            return ResponseEntity.notFound().build();
        }
    }

}