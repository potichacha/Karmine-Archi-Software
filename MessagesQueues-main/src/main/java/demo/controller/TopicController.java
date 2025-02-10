package demo.controller;

import demo.model.Topic;
import demo.model.Message;
import demo.service.TopicService;
import demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/topics")
@CrossOrigin(origins = "*") // Autorise toutes les requêtes CORS
public class TopicController {

    @Autowired
    private TopicService topicService;

    @Autowired
    private MessageService messageService;

    // 🔹 Récupérer tous les topics
    @GetMapping
    public ResponseEntity<List<Topic>> getAllTopics() {
        return ResponseEntity.ok(topicService.getAllTopics());
    }

    // 🔹 Récupérer un topic par ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getTopicById(@PathVariable Long id) {
        Optional<Topic> topicOpt = topicService.findById(id);
        if (topicOpt.isEmpty()) {
            return ResponseEntity.status(404).body("❌ Topic non trouvé.");
        }
        return ResponseEntity.ok(topicOpt.get());
    }

    // 🔹 Créer un topic
    @PostMapping
    public ResponseEntity<?> createTopic(@RequestParam String name) {
        try {
            Topic topic = topicService.createTopic(name);
            return ResponseEntity.ok(topic);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Erreur lors de la création du topic.");
        }
    }

    // 🔹 Ajouter un message à un topic
    @PostMapping("/{id}/messages/{messageId}")
    public ResponseEntity<?> addMessageToTopic(@PathVariable Long id, @PathVariable Long messageId) {
        Optional<Message> messageOpt = messageService.getMessageById(messageId);
        if (messageOpt.isEmpty()) {
            return ResponseEntity.status(404).body("❌ Message non trouvé.");
        }

        try {
            topicService.addMessageToTopic(id, messageOpt.get());
            return ResponseEntity.ok("✅ Message ajouté au topic !");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("❌ Erreur : " + e.getMessage());
        }
    }

    // 🔹 Supprimer un message d'un topic
    @DeleteMapping("/{id}/messages/{messageId}")
    public ResponseEntity<?> removeMessageFromTopic(@PathVariable Long id, @PathVariable Long messageId) {
        Optional<Message> messageOpt = messageService.getMessageById(messageId);
        if (messageOpt.isEmpty()) {
            return ResponseEntity.status(404).body("❌ Message non trouvé.");
        }

        try {
            topicService.removeMessageFromTopic(id, messageOpt.get());
            return ResponseEntity.ok("✅ Message retiré du topic.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("❌ Erreur : " + e.getMessage());
        }
    }

    // 🔹 Récupérer les messages d’un topic triés
    @GetMapping("/{id}/messages")
    public ResponseEntity<?> getMessagesFromTopic(@PathVariable Long id) {
        try {
            List<Message> messages = topicService.getMessagesFromTopic(id);
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("❌ Erreur : " + e.getMessage());
        }
    }
}