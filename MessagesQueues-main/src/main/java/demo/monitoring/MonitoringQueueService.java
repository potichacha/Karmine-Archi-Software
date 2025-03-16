package demo.monitoring;

import org.springframework.stereotype.Service;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class MonitoringQueueService {
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public void sendAlert(String message) {
        queue.add(message);
    }

    public String getNextAlert() {
        return queue.poll(); // Récupère le prochain message dans la queue
    }
}

