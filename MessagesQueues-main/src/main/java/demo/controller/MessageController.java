package demo.controller;

import demo.model.Message;
import demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // ✅ Endpoint pour créer un message (corrigé)
    @PostMapping
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        Message savedMessage = messageService.createMessage(message);
        return ResponseEntity.ok(savedMessage);
    }

    // ✅ Lecture d'un message (mise à jour des méta-données)
    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessage(@PathVariable Long id) {
        Message message = messageService.readMessage(id);
        return ResponseEntity.ok(message);
    }

    // ✅ Récupérer tous les messages
    @GetMapping
    public ResponseEntity<List<Message>> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    // ✅ Recherche de messages par contenu partiel
    @GetMapping("/search")
    public ResponseEntity<List<Message>> searchMessages(@RequestParam String content) {
        List<Message> messages = messageService.searchMessages(content);
        return ResponseEntity.ok(messages);
    }

    // ✅ Suppression d'un message (vérification faite dans le service)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long id) {
        try {
            messageService.deleteMessage(id);
            return ResponseEntity.ok("✅ Message supprimé avec succès !");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("❌ Erreur : " + e.getMessage());
        }
    }
}