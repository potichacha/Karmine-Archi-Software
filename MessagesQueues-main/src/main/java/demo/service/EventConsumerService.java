package demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EventConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumerService.class);
    
    @KafkaListener(topics = "message-events", groupId = "your-group-id")
    public void consumeEvent(String event) {
        logger.info("🔔 Event reçu : " + event);
        // Vous pouvez ici ajouter des actions en fonction des types d'événements (par ex. enregistrer dans un log, envoyer une notification, etc.)
    }
}
