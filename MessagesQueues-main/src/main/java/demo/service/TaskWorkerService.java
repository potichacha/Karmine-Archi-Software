package demo.service;

import demo.model.Task;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TaskWorkerService {

    private final TaskQueueService taskQueueService;
    private final TopicService topicService;

    public TaskWorkerService(TaskQueueService taskQueueService, TopicService topicService) {
        this.taskQueueService = taskQueueService;
        this.topicService = topicService;
    }

    @Scheduled(fixedDelay = 5000)
    public void processTasks() {
        Task task = taskQueueService.pollNextTask();
        if (task != null) {
            task.setStatus("IN_PROGRESS");
            try {
                if ("DELETE_TOPIC".equals(task.getType())) {
                    topicService.deleteTopic(task.getTopicId());
                }
                task.setStatus("DONE");
            } catch (Exception e) {
                task.setStatus("FAILED");
            }
        }
    }
}