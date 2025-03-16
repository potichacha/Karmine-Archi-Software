package demo.service;

import demo.model.Message;
import demo.model.Topic;
import demo.repository.MessageRepository;
import demo.repository.TopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final MessageRepository messageRepository;
    private final QueueService queueService;

    public TopicService(TopicRepository topicRepository, MessageRepository messageRepository, QueueService queueService) {
        this.topicRepository = topicRepository;
        this.messageRepository = messageRepository;
        this.queueService = queueService;
    }

    // ✅ Création d'un Topic
    public Topic createTopic(Topic topic) {
        return topicRepository.save(topic);
    }

    // ✅ Récupérer tous les Topics
    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    // ✅ Ajouter un message à un Topic et mettre à jour la queue
    public Topic addMessageToTopic(Long topicId, Message message) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("❌ Topic non trouvé : " + topicId));

        message = messageRepository.save(message); // ✅ Sauvegarde le message
        topic.getMessages().add(message); // ✅ Ajoute le message au Topic
        topicRepository.save(topic);

        // ✅ Mise à jour de la queue avec le dernier message
        queueService.updateQueue(topic, message);

        return topic;
    }

    // ✅ Récupérer les messages d'un Topic
    public List<Message> getMessagesFromTopic(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("❌ Topic non trouvé : " + topicId));
        return topic.getMessages();
    }

    // ✅ Supprimer un message d'un Topic et mettre à jour la queue
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

        // ✅ Si le message était le dernier dans la queue, on l'efface de la queue
        Message lastMessage = queueService.getLastMessage(topic);
        if (lastMessage != null && lastMessage.getId().equals(messageId)) {
            queueService.updateQueue(topic, null); // ✅ Supprime le message de la queue
        }

        // ✅ Si le message n'est plus dans aucun Topic, on le supprime de la base
        if (message.getTopics().isEmpty()) {
            messageRepository.delete(message);
        }

        return topic;
    }

    // ✅ Récupérer le dernier message d'un Topic
    public Message getLastMessageFromQueue(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("❌ Topic non trouvé : " + topicId));

        return queueService.getLastMessage(topic);
    }
}