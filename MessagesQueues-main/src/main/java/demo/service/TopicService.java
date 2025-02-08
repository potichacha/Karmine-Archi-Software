package demo.service;

import demo.data.TopicData;
import demo.model.Message;
import demo.model.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.stream.Collectors;
import demo.model.TopicMessage;

@Service
public class TopicService {

    @Autowired
    private TopicData topicData;

    /**
     * Crée un topic s'il n'existe pas déjà.
     */
    public Topic createTopic(String name) {
        return topicData.findByName(name) != null ? topicData.findByName(name) : topicData.save(new Topic(name));
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
            throw new IllegalArgumentException("Topic non trouvé");
        }
    }

    public List<Message> getMessagesFromTopic(Long topicId) {
        Optional<Topic> topicOpt = topicData.findById(topicId);
        if (topicOpt.isPresent()) {
            return topicOpt.get().getTopicMessages().stream()
                    .sorted(Comparator.comparingInt(TopicMessage::getMessageNumber))
                    .map(TopicMessage::getMessage)
                    .collect(Collectors.toList());
        }
        throw new IllegalArgumentException("Topic non trouvé");
    }

    /**
     * Supprime un message d'un topic. Si le message n'appartient plus à aucun topic, il est supprimé définitivement.
     */
    @Transactional
    public void removeMessageFromTopic(Long topicId, Message message) {
        Optional<Topic> topicOpt = topicData.findById(topicId);
        if (topicOpt.isPresent()) {
            Topic topic = topicOpt.get();
            topic.removeMessage(message);

            // Suppression du message s'il n'est plus associé à aucun topic
            if (message.getTopics().isEmpty()) {
                message.setQueue(null);
            }

            topicData.save(topic);
        } else {
            throw new IllegalArgumentException("Topic non trouvé");
        }
    }
}