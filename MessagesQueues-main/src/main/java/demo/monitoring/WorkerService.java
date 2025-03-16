package demo.monitoring;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class WorkerService {
    private final MonitoringQueueService queueService;

    public WorkerService(MonitoringQueueService queueService) {
        this.queueService = queueService;
    }

    @Scheduled(fixedRate = 5000) // Vérifie toutes les 5s
    public void processAlerts() {
        String alert = queueService.getNextAlert();

        if (alert != null) {
            System.out.println("[WORKER] 🔄 Traitement de l’alerte : " + alert);
            restartContainer();
        }
    }

    private void restartContainer() {
        System.out.println("[WORKER] 🚀 Redémarrage du container...");
        // Simule un restart (commande docker en vrai)
    }
}