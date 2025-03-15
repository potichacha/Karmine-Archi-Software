package demo.service;

import demo.model.Message;
import demo.model.MessageQueue;
import demo.model.Topic;
import demo.repository.MessageQueueRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QueueService {

    private final MessageQueueRepository messageQueueRepository;

    public QueueService(MessageQueueRepository messageQueueRepository) {
        this.messageQueueRepository = messageQueueRepository;
    }

    public void updateQueue(Topic topic, Message newMessage) {
        Optional<MessageQueue> existingQueue = messageQueueRepository.findByTopic(topic);

        if (existingQueue.isPresent()) {
            MessageQueue queue = existingQueue.get();
            queue.setLastMessage(newMessage);
            messageQueueRepository.save(queue);
        } else {
            messageQueueRepository.save(new MessageQueue(topic, newMessage));
        }
    }

    public Message getLastMessage(Topic topic) {
        return messageQueueRepository.findByTopic(topic)
                .map(MessageQueue::getLastMessage)
                .orElse(null);
    }
}
