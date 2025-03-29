package demo.service;

import demo.model.Task;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class TaskQueueService {
    private final Queue<Task> taskQueue = new LinkedBlockingQueue<>();
    private final Map<String, Task> taskMap = new ConcurrentHashMap<>();

    public String enqueueDeleteTopicTask(Long topicId) {
        String taskId = UUID.randomUUID().toString();
        Task task = new Task(taskId, "DELETE_TOPIC", "PENDING", topicId);
        taskQueue.add(task);
        taskMap.put(taskId, task);
        return taskId;
    }

    public Task getTaskStatus(String taskId) {
        return taskMap.get(taskId);
    }

    public Task pollNextTask() {
        return taskQueue.poll();
    }
}