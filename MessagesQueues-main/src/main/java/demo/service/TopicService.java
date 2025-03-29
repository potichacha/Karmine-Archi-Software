package demo.service;

import demo.model.Message;
import demo.model.Topic;
import demo.repository.MessageRepository;
import demo.repository.TopicRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TopicService {

    private static final Logger logger = LoggerFactory.getLogger(TopicService.class);
    private final TopicRepository topicRepository;
    private final MessageRepository messageRepository;
    private final QueueService queueService;
    private final EventPublisherService eventPublisherService;

    public TopicService(TopicRepository topicRepository, MessageRepository messageRepository, QueueService queueService, EventPublisherService eventPublisherService) {
        this.topicRepository = topicRepository;
        this.messageRepository = messageRepository;
        this.queueService = queueService;
        this.eventPublisherService = eventPublisherService;
    }

    // ✅ Création d'un Topic
    public Topic createTopic(Topic topic) {
        eventPublisherService.publishTopicCreatedEvent(topic.getName(),topic.getId());
        //logger.info("✅ Création d'un nouveau topic : {}", topic.getName());
        return topicRepository.save(topic);
    }

    // ✅ Récupérer tous les Topics
    public List<Topic> getAllTopics() {
        List<Topic> topics = topicRepository.findAll();
        logger.info("✅ {} topics récupérés depuis la base.", topics.size());
        return topics;
    }

    // ✅ Récupérer un topic par son ID
    public Optional<Topic> getTopicById(Long topicId) {
        Optional<Topic> topic = topicRepository.findById(topicId);
        if (topic.isEmpty()) {
            logger.warn("❌ Topic {} non trouvé.", topicId);
        }
        return topic;
    }

    // ✅ Récupérer un topic via son nom
    public Topic findTopicByName(String name) {
        return topicRepository.findByName(name).orElse(null);
    }

    // ✅ Ajouter un message à un Topic et mettre à jour la queue
    public Topic addMessageToTopic(Long topicId, Message message) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("❌ Topic non trouvé : " + topicId));

        message = messageRepository.save(message); // ✅ Sauvegarde le message
        topic.getMessages().add(message); // ✅ Ajoute le message au Topic
        topicRepository.save(topic);

        // ✅ Mise à jour de la queue avec le dernier message
        queueService.updateQueue(topic, message);

        //logger.info("✅ Message ajouté au topic {}.", topicId);
        eventPublisherService.publishTopicAddMessage(topic.getName(), topicId);
        return topic;
    }

    // ✅ Récupérer les messages d'un Topic
    public List<Message> getMessagesFromTopic(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("❌ Topic non trouvé : " + topicId));
        logger.info("✅ {} messages récupérés pour le topic {}.", topic.getMessages().size(), topicId);
        eventPublisherService.publishedTopicGetMessagesFromTopic(topic.getName(), topicId);
        return topic.getMessages();
    }

    // ✅ Supprimer un message d'un Topic et mettre à jour la queue
    public Topic removeMessageFromTopic(Long topicId, Long messageId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("❌ Topic non trouvé : " + topicId));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("❌ Message non trouvé : " + messageId));

        if (!topic.getMessages().contains(message)) {
            eventPublisherService.publishMessageError("Message ID: " + messageId +"n'est pas dans le Topic ID: " + topicId);
            throw new RuntimeException("❌ Le message " + messageId + " n'est pas dans ce Topic.");
        }

        // ✅ Vérification avant suppression
        if (message.getNumberOfReads() == 0) {
            eventPublisherService.publishMessageError("Impossible de supprimer un message non lu. Message ID: " + messageId);
            throw new RuntimeException("❌ Impossible de supprimer un message non lu.");
        }

        topic.getMessages().remove(message);
        topicRepository.save(topic);

        // ✅ Si le message était le dernier dans la queue, on l'efface de la queue
        Message lastMessage = queueService.getLastMessage(topic);
        if (lastMessage != null && lastMessage.getId().equals(messageId)) {
            queueService.updateQueue(topic, null); // ✅ Supprime le message de la queue
        }

        // ✅ Supprimer le message de la base seulement s'il n'est plus dans aucun topic
        if (message.getTopics().isEmpty()) {
            messageRepository.delete(message);
        }

        return topic;
    }

    // ✅ Récupérer le dernier message d'un Topic
    public Message getLastMessageFromQueue(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("❌ Topic non trouvé : " + topicId));

        Message lastMessage = queueService.getLastMessage(topic);
        if (lastMessage == null) {
            logger.info("✅ Aucun message dans la queue pour le topic {}.", topicId);
        }
        return lastMessage;
    }

    // ✅ Supprimer un Topic en gérant les contraintes
    public void deleteTopic(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("❌ Topic non trouvé : " + topicId));

        // ✅ Cloner la liste pour éviter la modification en direct lors de l'itération
        List<Message> messagesToRemove = new ArrayList<>(topic.getMessages());

        // ✅ Dissocier chaque message du topic
        for (Message message : messagesToRemove) {
            message.getTopics().remove(topic);
            if (message.getTopics().isEmpty()) { // Si ce message n'est plus lié à aucun topic
                messageRepository.delete(message); // Supprime le message
            } else {
                messageRepository.save(message); // Sauvegarde le message mis à jour
            }
        }

        // ✅ Supprimer le topic après avoir dissocié les messages
        eventPublisherService.publishTopicDeletedEvent(topic.getName(), topicId);
        topicRepository.delete(topic);
    }
}