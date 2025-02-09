package demo.controller;

import demo.model.Topic;
import demo.model.Message;
import demo.service.TopicService;
import demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/topics")
public class TopicController {

    @Autowired
    private TopicService topicService;

    @Autowired
    private MessageService messageService;

    @PostMapping
    public ResponseEntity<Topic> createTopic(@RequestParam String name) {
        return ResponseEntity.ok(topicService.createTopic(name));
    }

    @PostMapping("/{id}/messages/{messageId}")
    public ResponseEntity<String> addMessageToTopic(@PathVariable Long id, @PathVariable Long messageId) {
        Message message = messageService.getMessageById(messageId);
        if (message == null) {
            return ResponseEntity.badRequest().body("Message non trouvé.");
        }
        topicService.addMessageToTopic(id, message);
        return ResponseEntity.ok("Message ajouté au topic");
    }

    @DeleteMapping("/{id}/messages/{messageId}")
    public ResponseEntity<String> removeMessageFromTopic(@PathVariable Long id, @PathVariable Long messageId) {
        Message message = messageService.getMessageById(messageId);
        if (message == null) {
            return ResponseEntity.badRequest().body("Message non trouvé.");
        }
        topicService.removeMessageFromTopic(id, message);
        return ResponseEntity.ok("Message retiré du topic");
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<Message>> getMessagesFromTopic(@PathVariable Long id) {
        return ResponseEntity.ok(topicService.getMessagesFromTopic(id));
    }
}