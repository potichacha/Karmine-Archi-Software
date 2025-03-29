package demo.service;

import demo.model.Message;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventPublisherService {

    private static final Logger logger = LoggerFactory.getLogger(EventPublisherService.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    public EventPublisherService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Crée le payload JSON pour l'événement MessageCreated
    private String createEventPayload(Message message, String eventType) {
        return "{\"type\": \"" + eventType + "\", \"messageId\": " + message.getId() +
                ", \"content\": \"" + message.getContent() + "\", \"timeCreated\": \"" + message.getTimeCreated() + "\"}";
    }
    private String createEventError(String message, String eventType) {
        return "{\"type\": \"" + eventType + "\", \"message\": \"" + message + "\", \"timeCreated\": \"" + LocalDateTime.now() + "\"}";
    }

    //----------------------Message---------------------

    // ✅ Publie un événement lorsque qu'un message est créé
    public void publishMessageCreatedEvent(Message message) {
        String eventPayload = createEventPayload(message, "MessageCreated");
        kafkaTemplate.send("message-events", eventPayload);
        logger.info("✅ MessageCreated event published: " + eventPayload);
    }

    // ✅ Publie un événement lorsque qu'un message est supprimé
    public void publishMessageDeletedEvent(Long messageId) {
        String eventPayload = "{\"type\": \"MessageDeleted\", \"messageId\": " + messageId + "}";
        kafkaTemplate.send("message-events", eventPayload);
        logger.info("✅ MessageDeleted event published: " + eventPayload);
    }

    public void publishMessageReadEvent(Long messageId) {
        String eventPayload = "{\"type\": \"MessageRead\", \"messageId\": " + messageId + "}";
        kafkaTemplate.send("message-events", eventPayload);
        logger.info("✅ MessageRead event published: " + eventPayload);
    }

    //----------------------Topic---------------------

    public void publishTopicCreatedEvent(String topicName, Long topicId) {
        String eventPayload = "{\"type\": \"TopicCreated\", \"topicName\": \"" + topicName + "\", \"topicId\": \"" + topicId + "\"}";
        kafkaTemplate.send("topic-events", eventPayload);
        logger.info("✅ TopicCreated event published: " + eventPayload);
    }

    public void publishTopicDeletedEvent(String topicName, Long topicId) {
        String eventPayload = "{\"type\": \"TopicDeleted\", \"topicName\": \"" + topicName + "\", \"topicId\": \"" + topicId + "\"}";
        kafkaTemplate.send("topic-events", eventPayload);
        logger.info("✅ TopicDeleted event published: " + eventPayload);
    }

    public void publishTopicAddMessage(String topicName, Long topicId) {
        String eventPayload = "{\"type\": \"TopicAddMessage\", \"topicName\": \"" + topicName + "\", \"topicId\": \"" + topicId + "\"}";
        kafkaTemplate.send("topic-events", eventPayload);
        logger.info("✅ Adding Message to Topic "+ topicId +" "+ eventPayload);
    }

    public void publishTopicRemoveMessage(String topicName, Long topicId) {
        String eventPayload = "{\"type\": \"TopicRemoveMessage\", \"topicName\": \"" + topicName + "\", \"topicId\": \"" + topicId + "\"}";
        kafkaTemplate.send("topic-events", eventPayload);
        logger.info("✅ Removing Message from Topic "+ topicId +" "+ eventPayload);
    }

    public void publishedTopicGetMessagesFromTopic(String topicName, Long topicId) {
        String eventPayload = "{\"type\": \"TopicGetMessagesFromTopic\", \"topicName\": \"" + topicName + "\", \"topicId\": \"" + topicId + "\"}";
        kafkaTemplate.send("topic-events", eventPayload);
        logger.info("✅ Getting Messages from Topic "+ topicId +" "+ eventPayload);
    }

    //----------------------Error---------------------
    public void publishMessageError(String message) {
        String eventPayload = createEventError(message, "ErrorMessage");
        kafkaTemplate.send("message-events", eventPayload);
        logger.info("✅ ErrorMessage event published: " + eventPayload);
    }
}
