package demo.monitoring;

import org.springframework.stereotype.Service;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MonitoringQueueService {
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public void sendAlert(String message) {
        queue.add(message);
        System.out.println("[QUEUE] Alerte ajoutée : " + message);
    }

    public String getNextAlert() {
        return queue.poll(); // Récupère le prochain message dans la queue
    }

    public List<String> getAllAlerts() {
        return queue.stream().collect(Collectors.toList());
    }
}