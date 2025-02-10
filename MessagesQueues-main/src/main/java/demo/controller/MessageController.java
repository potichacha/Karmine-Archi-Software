package demo.controller;

import demo.model.Message;
import demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/messages") // ✅ Route principale pour gérer les messages
public class MessageController {

    @Autowired
    private MessageService messageService;

    // 🔹 Créer un message
    @PostMapping
    public ResponseEntity<Message> createMessage(@RequestParam String content) {
        Message message = new Message(content, 0);
        return ResponseEntity.ok(messageService.saveMessage(message));
    }

    // 🔹 Récupérer tous les messages
    @GetMapping
    public ResponseEntity<List<Message>> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    // 🔹 Récupérer un message par ID
    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable Long id) {
        return messageService.getMessageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Supprimer un message si lu et non lié à un topic
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMessage(@PathVariable Long id) {
        try {
            boolean deleted = messageService.deleteMessage(id);
            return deleted ? ResponseEntity.ok("✅ Message supprimé.") : ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}