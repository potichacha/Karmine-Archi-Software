package com.example.controller;

import com.example.model.Message;
import com.example.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // Lecture d'un message (mise à jour des méta-données)
    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessage(@PathVariable Long id) {
        Message message = messageService.readMessage(id);
        return ResponseEntity.ok(message);
    }

    // Recherche de messages par contenu partiel
    @GetMapping("/search")
    public ResponseEntity<List<Message>> searchMessages(@RequestParam String content) {
        List<Message> messages = messageService.searchMessages(content);
        return ResponseEntity.ok(messages);
    }

    // Suppression d'un message (vérification faite dans le service)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return ResponseEntity.ok().build();
    }
}