package demo.service;

import demo.model.Message;
import demo.model.MessageQueue;
import demo.data.MessageQueueData;
import demo.data.MessageData;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageQueueData queueData;

    @Autowired
    private MessageData messageData;

    /**
     * Récupère toutes les files d'attente.
     */
    public List<MessageQueue> getAllQueues() {
        return queueData.findAll();
    }

    /**
     * Ajoute un message à une file d'attente.
     * Si la file n'existe pas, elle est créée.
     */
    @Transactional
    public Message addMessageToQueue(String queueId, Message message) {
        MessageQueue queue = queueData.findById(queueId).orElseGet(() -> {
            MessageQueue newQueue = new MessageQueue(queueId);
            return queueData.save(newQueue);
        });

        message.setQueue(queue);
        queue.addMessage(message);

        return messageData.save(message);
    }

    /**
     * Récupère le prochain message disponible dans une file, sans le supprimer.
     */
    public Message getNextMessage(String queueId) {
        List<Message> messages = messageData.findByQueueIdAndAvailableAtBefore(queueId, LocalDateTime.now());
        return messages.isEmpty() ? null : messages.get(0);
    }

    public List<Message> searchMessages(String searchTerm) {
        return messageData.findAll().stream()
                .filter(m -> m.getContent().contains(searchTerm))
                .collect(Collectors.toList());
    }

    public List<Message> getMessagesFrom(Long messageId) {
        return messageData.findAll().stream()
                .filter(m -> m.getId() >= messageId)
                .collect(Collectors.toList());
    }
    public Message getMessageById(Long id) {
        return messageData.findById(id).orElse(null);
    }

    /**
     * Supprime un message uniquement s'il est lu et n'est plus dans aucun topic.
     */
    @Transactional
    public boolean deleteMessage(Long messageId) {
        return messageData.findById(messageId).map(message -> {
            if (!message.isRead() || !message.getTopics().isEmpty()) {
                throw new IllegalStateException("Le message doit être lu et ne doit appartenir à aucun topic pour être supprimé.");
            }
            messageData.delete(message);
            return true;
        }).orElse(false);
    }
}
