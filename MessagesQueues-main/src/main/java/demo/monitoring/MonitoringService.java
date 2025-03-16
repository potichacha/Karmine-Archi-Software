package demo.monitoring;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Service
public class MonitoringService {
    private final MonitoringQueueService queueService;

    public MonitoringService(MonitoringQueueService queueService) {
        this.queueService = queueService;
    }

    @Scheduled(fixedRate = 10000) // Vérifie l’état toutes les 10s
    public void checkContainers() {
        boolean isContainerDown = Math.random() < 0.3;

        if (isContainerDown) {
            queueService.sendAlert("❌ Container down détecté automatiquement !");
            System.out.println("[MONITORING] Alerte envoyée automatiquement !");
        }
    }
}