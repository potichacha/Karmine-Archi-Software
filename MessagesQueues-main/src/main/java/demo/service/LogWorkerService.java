package demo.service;

import demo.model.Message;
import demo.model.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class LogWorkerService {

    private static final Logger logger = LoggerFactory.getLogger(LogWorkerService.class);
    private final TopicService topicService;
    private final MessageService messageService;

    public LogWorkerService(TopicService topicService, MessageService messageService) {
        this.topicService = topicService;
        this.messageService = messageService;
    }

    @Scheduled(fixedRate = 60000) // Envoi d'un log toutes les 60 secondes
    public void performTask() {
        String logMessage = "Worker task performed at " + java.time.LocalDateTime.now();
        logger.info(logMessage);
        sendLog(logMessage, "test");
    }

    public void sendLog(String logMessage, String type) {
        Topic logsTopic = topicService.findTopicByName("logs");
        if (logsTopic != null) {
            Message message_template = new Message();
            ArrayList<Topic> topics = new ArrayList<>();
            topics.add(logsTopic);
            message_template.setTopics(topics);
            message_template.setContent("{\"type\": \"" + type + "\" ,\"msg\": " + logMessage + "\"}");
            Message message = messageService.createMessage(message_template);
            topicService.addMessageToTopic(logsTopic.getId(), message);
        } else {
            logger.error("❌ Topic 'logs' non trouvé.");
        }
    }
}
