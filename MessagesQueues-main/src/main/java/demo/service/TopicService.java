package demo.service;

import demo.data.TopicData;
import demo.model.Message;
import demo.model.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TopicService {

    @Autowired
    private TopicData topicData;

    /**
     * 🔹 Récupère tous les topics.
     */
    public List<Topic> getAllTopics() {
        return topicData.findAll();
    }

    /**
     * 🔹 Récupère un topic par son ID.
     */
    public Optional<Topic> findById(Long id) {
        return topicData.findById(id);
    }

    /**
     * 🔹 Crée un topic s'il n'existe pas déjà.
     */
    public Topic createTopic(String name) {
        return topicData.findByName(name)
                .orElseGet(() -> topicData.save(new Topic(name)));
    }

    /**
     * 🔹 Ajoute un message à un topic.
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
     * 🔹 Récupère la liste des messages d'un topic triés par ID.
     */
    public List<Message> getMessagesFromTopic(Long topicId) {
        Topic topic = topicData.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("❌ Topic non trouvé"));

        return topic.getMessages().stream()
                .sorted((m1, m2) -> Long.compare(m1.getId(), m2.getId()))  // Tri des messages par ID croissant
                .collect(Collectors.toList());
    }

    /**
     * 🔹 Supprime un message d'un topic.
     *    Si le message n'est plus lié à aucun topic après suppression, il peut être supprimé.
     */
    @Transactional
    public void removeMessageFromTopic(Long topicId, Message message) {
        Optional<Topic> topicOpt = topicData.findById(topicId);
        if (topicOpt.isPresent()) {
            Topic topic = topicOpt.get();
            topic.removeMessage(message);

            // Vérifie si le message n'appartient plus à aucun topic
            if (message.getTopics().isEmpty()) {
                // messageData.delete(message);  // 🔥 Optionnel : suppression définitive si plus lié à aucun topic
            }

            topicData.save(topic);
        } else {
            throw new IllegalArgumentException("❌ Topic non trouvé");
        }
    }
}