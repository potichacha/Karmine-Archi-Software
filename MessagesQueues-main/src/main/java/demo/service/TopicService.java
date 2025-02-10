package demo.service;

import demo.data.TopicData;
import demo.model.Message;
import demo.model.Topic;
import demo.model.TopicMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class TopicService {

    @Autowired
    private TopicData topicData;

    /**
     * Récupère tous les topics.
     */
    public List<Topic> getAllTopics() {
        return topicData.findAll();
    }

    /**
     * Récupère un topic par son ID.
     */
    public Optional<Topic> findById(Long id) {
        return topicData.findById(id);
    }

    /**
     * Crée un topic s'il n'existe pas déjà.
     */
    public Topic createTopic(String name) {
        return topicData.findByName(name)
                .orElseGet(() -> topicData.save(new Topic(name)));
    }

    /**
     * Ajoute un message à un topic.
     */
    @Transactional
    public void addMessageToTopic(Long topicId, Message message) {
        Optional<Topic> topicOpt = topicData.findById(topicId);
        if (topicOpt.isPresent()) {
            Topic topic = topicOpt.get();
            topic.addMessage(message);
            topicData.save(topic);
        } else {
            throw new IllegalArgumentException("❌ Topic non trouvé");
        }
    }

    /**
     * Récupère la liste des messages d'un topic triés par numéro.
     */
    public List<Message> getMessagesFromTopic(Long topicId) {
        Topic topic = topicData.findByIdWithMessagesSorted(topicId)
                .orElseThrow(() -> new IllegalArgumentException("❌ Topic non trouvé"));  // ✅ Correction ici

        return topic.getTopicMessages().stream()
                .sorted(Comparator.comparingInt(TopicMessage::getMessageNumber))
                .map(TopicMessage::getMessage)
                .collect(Collectors.toList());
    }

    /**
     * Supprime un message d'un topic.
     */
    @Transactional
    public void removeMessageFromTopic(Long topicId, Message message) {
        Optional<Topic> topicOpt = topicData.findById(topicId);
        if (topicOpt.isPresent()) {
            Topic topic = topicOpt.get();
            topic.removeMessage(message);
            topicData.save(topic);
        } else {
            throw new IllegalArgumentException("❌ Topic non trouvé");
        }
    }
}