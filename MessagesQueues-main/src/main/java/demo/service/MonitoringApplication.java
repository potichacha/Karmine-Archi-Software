package demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

@EnableScheduling
public class MonitoringApplication implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(MonitoringApplication.class);
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String DOCKER_API_URL = "http://localhost:2375/containers/json?all=true";

    private static final List<String> CONTAINERS_TO_MONITOR = Arrays.asList("container1", "container2");

    public static void main(String[] args) {
        SpringApplication.run(MonitoringApplication.class, args);
    }

    @Override
    public void run(String... args) {
        logger.info("🚀 Monitoring Service Started.");
    }

    @Scheduled(fixedRate = 30000)
    public void checkContainerHealth() {
        try {
            // Call Docker API
            String response = restTemplate.getForObject(DOCKER_API_URL, String.class);
            JsonNode containers = objectMapper.readTree(response);

            logger.info("📡 Checking Docker containers...");

            for (JsonNode container : containers) {
                String containerId = container.get("Id").asText();
                String containerName = container.get("Names").get(0).asText().replace("/", "");

                logger.info("🔎 Checking container: ID={}, Name={}", containerId, containerName);

                JsonNode healthNode = container.path("State").path("Health");
                String healthStatus = healthNode.has("Status") ? healthNode.get("Status").asText() : "unknown";

                if (CONTAINERS_TO_MONITOR.contains(containerName)) {
                    if ("healthy".equals(healthStatus)) {
                        logger.info("✅ Container {} is healthy.", containerName);
                    } else {
                        logger.warn("⚠️ Container {} is unhealthy! Restarting...", containerName);
                        restartContainer(containerId);
                    }
                } else {
                    logger.info("❌ Skipping container {} (not in monitoring list)", containerName);
                }
            }
        } catch (Exception e) {
            logger.error("❌ Error checking container health: {}", e.getMessage());
        }
    }

    private void restartContainer(String containerId) {
        try {
            String restartUrl = "http://localhost:2375/containers/" + containerId + "/restart";
            restTemplate.postForEntity(restartUrl, null, String.class);
            logger.info("🔄 Restarted container: {}", containerId);
        } catch (Exception e) {
            logger.error("❌ Failed to restart container {}: {}", containerId, e.getMessage());
        }
    }
}
