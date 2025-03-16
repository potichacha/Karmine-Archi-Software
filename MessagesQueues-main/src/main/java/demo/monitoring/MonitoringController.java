package demo.monitoring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/monitoring")
public class MonitoringController {
    private final MonitoringQueueService queueService;

    public MonitoringController(MonitoringQueueService queueService) {
        this.queueService = queueService;
    }

    @GetMapping("/simulate-down")
    public String simulateContainerDown() {
        queueService.sendAlert("❌ Container down détecté via API !");
        return "Alerte envoyée !";
    }

    @GetMapping("/queue")
    public List<String> getAllAlerts() {
        return queueService.getAllAlerts();
    }
}
