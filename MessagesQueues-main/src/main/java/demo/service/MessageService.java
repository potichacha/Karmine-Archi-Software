package demo.service;

import demo.model.Message;
import demo.repository.MessageRepository;
import org.springframework.stereotype.Service;
import demo.model.Topic;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import demo.repository.TopicRepository;

@Service
public class MessageService {
    private final TopicRepository topicRepository;
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository, TopicRepository topicRepository) {
        this.messageRepository = messageRepository;
        this.topicRepository = topicRepository;
    }

    public Message createMessage(Message message) {
        message.setTimeCreated(LocalDateTime.now());
        return messageRepository.save(message);
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Optional<Message> getMessageById(Long id) {
        return messageRepository.findById(id);
    }
    public Message readMessage(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message non trouvé"));

        if (message.getTimeFirstAccessed() == null) {
            message.setTimeFirstAccessed(LocalDateTime.now());
        }

        // Incrémentation du nombre de lectures
        message.setNumberOfReads(message.getNumberOfReads() + 1);
        return messageRepository.save(message);
    }

    public List<Message> searchMessages(String content) {
        return messageRepository.findByContentContaining(content);
    }

    public void deleteMessage(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Message non trouvé : " + id));

        if (message.getNumberOfReads() == 0) {
            throw new RuntimeException("❌ Impossible de supprimer un message non lu.");
        }

        // 🚀 Retirer le message de tous les topics avant suppression
        for (Topic topic : message.getTopics()) {
            topic.getMessages().remove(message);
            topicRepository.save(topic);
        }
        messageRepository.delete(message);
    }
}