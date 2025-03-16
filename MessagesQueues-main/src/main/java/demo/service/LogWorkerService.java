package demo.service;

import demo.model.Message;
import demo.model.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class LogWorkerService {

    private static final Logger logger = LoggerFactory.getLogger(LogWorkerService.class);
    private final TopicService topicService;
    private final MessageService messageService;

    public LogWorkerService(TopicService topicService, MessageService messageService) {
        this.topicService = topicService;
        this.messageService = messageService;
    }

    @Scheduled(fixedRate = 5000) // Envoi d'un log toutes les 5 secondes
    public void performTask() {
        String logMessage = "Worker task performed at " + java.time.LocalDateTime.now();
        logger.info(logMessage);
        sendLog(logMessage);
    }

    private void sendLog(String logMessage) {
        Topic logsTopic = topicService.findTopicByName("logs");
        if (logsTopic != null) {
            Message message = new Message();
            message.setContent(logMessage);
            message = messageService.createMessage(message);
            topicService.addMessageToTopic(logsTopic.getId(), message);
        } else {
            logger.error("❌ Topic 'logs' non trouvé.");
        }
    }
}
