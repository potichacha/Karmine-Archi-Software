package demo.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import demo.model.MessageQueue;
import demo.model.Message;

@RestController
@RequestMapping("/queues")
public class MessageQueueController {
    private static Map<String, MessageQueue> queuesMap = new HashMap<>();
    static {
        Message hi = new Message(1, "Hi");
        Message hello = new Message(2, "Hello");
        MessageQueue queue = new MessageQueue();
        queue.setId("main");
        queuesMap.put(queue.getId(), queue);
        queue.addMessage(hi);
        queue.addMessage(hello);

        Message bonjour = new Message(3, "Bonjour");
        MessageQueue queue2 = new MessageQueue();
        queue2.setId("secondary");
        queuesMap.put(queue2.getId(), queue2);
        queue2.addMessage(bonjour);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Collection<MessageQueue>> getProduct(
            @RequestParam(value = "startWith", defaultValue = "") String prefix) {
        return new ResponseEntity<>(
                queuesMap.values().stream().filter(p -> p.getId().startsWith(prefix)).toList(),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<MessageQueue> get(@PathVariable("id") String id) {
        if (queuesMap.containsKey(id)) {
            return new ResponseEntity<>(queuesMap.get(id), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/{id}/messages", method = RequestMethod.GET)
    public ResponseEntity<Collection<Message>> getMessages(@PathVariable("id") String id) {
        if (queuesMap.containsKey(id)) {
            return new ResponseEntity<>(queuesMap.get(id).getMessages(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/{id}/messages/next", method = RequestMethod.GET)
    public ResponseEntity<Message> getNextMessage(@PathVariable("id") String id) {
        if (queuesMap.containsKey(id) && queuesMap.get(id).getMessages().size() > 0) {
            return new ResponseEntity<>(queuesMap.get(id).nextMessage(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
}
