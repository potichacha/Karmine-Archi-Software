package com.example.karmine.service;

import com.example.karmine.model.Message;
import com.example.karmine.model.MessageQueue;
import com.example.karmine.data.MessageQueueData;
import com.example.karmine.data.MessageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageQueueData queueData;

    @Autowired
    private MessageData messageData;

    public List<MessageQueue> getAllQueues() {
        return queueData.findAll();
    }

    public Message addMessageToQueue(String queueId, Message message) {
        // Chercher ou créer la file d'attente
        MessageQueue queue = queueData.findById(queueId).orElseGet(() -> {
            MessageQueue newQueue = new MessageQueue(queueId);
            queueData.save(newQueue);
            return newQueue;
        });

        // Ajouter le message à la file
        message.setQueue(queue);
        queue.addMessage(message);

        // Sauvegarder la file et le message
        queueData.save(queue);
        return message;
    }

    public Message getNextMessage(String queueId) {
        // Trouver les messages disponibles dans la file
        List<Message> messages = messageData.findByQueueIdAndAvailableAtBefore(queueId, LocalDateTime.now());
        if (messages.isEmpty()) {
            return null; // Aucun message disponible
        }

        // Retourner le premier message disponible
        Message nextMessage = messages.get(0);
        messageData.delete(nextMessage); // Supprimer après traitement
        return nextMessage;
    }
}