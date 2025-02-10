package demo.service;

import demo.model.Message;
import demo.model.MessageQueue;
import demo.data.MessageQueueData;
import demo.data.MessageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
     * Crée et sauvegarde un message sans l'ajouter à une file d'attente.
     */
    public Message saveMessage(Message message) {
        return messageData.save(message);
    }

    /**
     * Récupère tous les messages.
     */
    public List<Message> getAllMessages() {
        return messageData.findAll();
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
        return messageData.findByQueueIdAndAvailableAtBefore(queueId, LocalDateTime.now())
                .stream().findFirst().orElse(null);
    }

    /**
     * Recherche des messages contenant un terme spécifique.
     */
    public List<Message> searchMessages(String searchTerm) {
        return messageData.findByContentContainingIgnoreCase(searchTerm);
    }

    /**
     * Récupère les messages à partir d'un ID donné.
     */
    public List<Message> getMessagesFrom(Long messageId) {
        return messageData.findByIdGreaterThan(messageId);
    }

    /**
     * Récupère un message par ID.
     */
    public Optional<Message> getMessageById(Long messageId) {
        return messageData.findById(messageId);
    }

    /**
     * Supprime un message uniquement s'il est lu et n'est plus dans aucun topic.
     */
    @Transactional
    public boolean deleteMessage(Long messageId) {
        return messageData.findById(messageId).map(message -> {
            if (!message.isRead()) {
                throw new IllegalStateException("❌ Le message doit être lu avant suppression.");
            }
            messageData.delete(message);
            return true;
        }).orElse(false);
    }
}