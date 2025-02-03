package demo.controller;

import demo.model.Message;
import demo.model.MessageQueue;
import demo.controller.MessageQueueRequest;
import demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/queues")
public class MessageQueueController {

    @Autowired
    private MessageService messageService;

    @GetMapping
    public ResponseEntity<List<MessageQueue>> getAllQueues() {
        return ResponseEntity.ok(messageService.getAllQueues());
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<Message> addMessage(
            @PathVariable String id, @RequestBody MessageQueueRequest request) {
        Message message = new Message(request.getContent(), request.getDelay());
        return ResponseEntity.ok(messageService.addMessageToQueue(id, message));
    }

    @GetMapping("/{id}/messages/next")
    public ResponseEntity<Message> getNextMessage(@PathVariable String id) {
        return ResponseEntity.ok(messageService.getNextMessage(id));
    }
}
