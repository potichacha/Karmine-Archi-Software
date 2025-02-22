package com.example.service;

import com.example.model.Message;
import com.example.model.Topic;
import com.example.repository.MessageRepository;
import com.example.repository.TopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final MessageRepository messageRepository;

    public TopicService(TopicRepository topicRepository, MessageRepository messageRepository) {
        this.topicRepository = topicRepository;
        this.messageRepository = messageRepository;
    }

    public Topic createTopic(Topic topic) {
        return topicRepository.save(topic);
    }

    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    // 🚀 Ajouter un message à un Topic
    public Topic addMessageToTopic(Long topicId, Message message) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("❌ Topic non trouvé : " + topicId));

        message = messageRepository.save(message); // ✅ Sauvegarde le message
        topic.getMessages().add(message); // ✅ Ajoute le message au Topic
        return topicRepository.save(topic);
    }

    // 🚀 Récupérer les messages d'un Topic
    public List<Message> getMessagesFromTopic(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("❌ Topic non trouvé : " + topicId));
        return topic.getMessages();
    }

    public Topic removeMessageFromTopic(Long topicId, Long messageId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("❌ Topic non trouvé : " + topicId));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("❌ Message non trouvé : " + messageId));

        if (!topic.getMessages().contains(message)) {
            throw new RuntimeException("❌ Le message " + messageId + " n'est pas dans ce Topic.");
        }

        topic.getMessages().remove(message);
        topicRepository.save(topic);

        // 🚀 Si le message n'est plus dans aucun Topic, on le supprime
        if (message.getTopics().isEmpty()) {
            messageRepository.delete(message);
        }

        return topic;
    }
}