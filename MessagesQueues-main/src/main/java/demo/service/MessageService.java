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
    private final EventPublisherService eventPublisherService;

    public MessageService(MessageRepository messageRepository, TopicRepository topicRepository,EventPublisherService eventPublisherService) {
        this.messageRepository = messageRepository;
        this.topicRepository = topicRepository;
        this.eventPublisherService = eventPublisherService;
    }

    // ✅ Création d'un message (avec gestion des Topics)
    public Message createMessage(Message message) {
        message.setTimeCreated(LocalDateTime.now());

        // Vérification si les Topics existent
        for (Topic topic : message.getTopics()) {
            Optional<Topic> existingTopic = topicRepository.findById(topic.getId());
            if (existingTopic.isEmpty()) {
                throw new RuntimeException("❌ Topic inexistant : " + topic.getId());
            }
        }
        eventPublisherService.publishMessageCreatedEvent(messageRepository.save(message));
        return messageRepository.save(message);
    }

    // ✅ Récupérer tous les messages
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    // ✅ Lire un message (et mettre à jour les métadonnées)
    public Message readMessage(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Message non trouvé !"));

        if (message.getTimeFirstAccessed() == null) {
            message.setTimeFirstAccessed(LocalDateTime.now());
        }

        // Incrémentation du nombre de lectures
        message.setNumberOfReads(message.getNumberOfReads() + 1);
        eventPublisherService.publishMessageReadEvent(message.getId());
        return messageRepository.save(message);
    }

    // ✅ Recherche de messages par contenu
    public List<Message> searchMessages(String content) {
        return messageRepository.findByContentContaining(content);
    }

    // ✅ Suppression avec règle : Impossible de supprimer un message non lu
    public void deleteMessage(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Message non trouvé : " + id));

        if (message.getNumberOfReads() == 0) {
            eventPublisherService.publishMessageError("Impossible de supprimer un message non lu. Message ID: " + id);
            throw new RuntimeException("❌ Impossible de supprimer un message non lu.");
        }

        // ✅ Suppression du message de tous ses topics avant suppression
        for (Topic topic : message.getTopics()) {
            topic.getMessages().remove(message);
            topicRepository.save(topic);
        }

        eventPublisherService.publishMessageDeletedEvent(message.getId());
        messageRepository.delete(message);
    }
}