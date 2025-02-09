package demo.controller;

import demo.model.Message;
import demo.model.MessageQueue;
import demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/queues")
public class MessageQueueController {

    @Autowired
    private MessageService messageService;

    /**
     * Récupère toutes les files d'attente.
     */
    @GetMapping
    public ResponseEntity<List<MessageQueue>> getAllQueues() {
        return ResponseEntity.ok(messageService.getAllQueues());
    }

    /**
     * Ajoute un message dans une file d'attente donnée.
     */
    @PostMapping("/{id}/messages")
    public ResponseEntity<Message> addMessage(
            @PathVariable String id, @RequestBody MessageQueueRequest request) {
        Message message = new Message(request.getContent(), request.getDelay());
        return ResponseEntity.ok(messageService.addMessageToQueue(id, message));
    }

    /**
     * Récupère le prochain message disponible dans une file.
     */
    @GetMapping("/{id}/messages/next")
    public ResponseEntity<Message> getNextMessage(@PathVariable String id) {
        Message message = messageService.getNextMessage(id);
        return message != null ? ResponseEntity.ok(message) : ResponseEntity.noContent().build();
    }

    /**
     * Supprime un message uniquement s'il est lu et n'est plus dans aucun topic.
     */
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<String> deleteMessage(@PathVariable Long messageId) {
        try {
            boolean deleted = messageService.deleteMessage(messageId);
            return deleted ? ResponseEntity.ok("Message supprimé.") : ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Recherche de messages contenant un mot-clé dans leur contenu.
     */
    @GetMapping("/messages/search")
    public ResponseEntity<List<Message>> searchMessages(@RequestParam String query) {
        List<Message> messages = messageService.searchMessages(query);
        return messages.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(messages);
    }

    /**
     * Récupère une liste de messages à partir d'un numéro donné.
     */
    @GetMapping("/messages/from/{startId}")
    public ResponseEntity<List<Message>> getMessagesFrom(@PathVariable Long startId) {
        List<Message> messages = messageService.getMessagesFrom(startId);
        return messages.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(messages);
    }
}