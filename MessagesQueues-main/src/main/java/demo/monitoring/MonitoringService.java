package demo.monitoring;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MonitoringService {
    private final MonitoringQueueService queueService;

    public MonitoringService(MonitoringQueueService queueService) {
        this.queueService = queueService;
    }

    @Scheduled(fixedRate = 10000) // Vérifie l’état toutes les 10s
    public void checkContainers() {
        // Simulation d’un container down
        boolean isContainerDown = Math.random() < 0.3;

        if (isContainerDown) {
            queueService.sendAlert("❌ Container down détecté !");
            System.out.println("Alerte envoyée !");
        }
    }
}

