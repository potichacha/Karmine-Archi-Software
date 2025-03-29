package demo.controller;

import demo.model.Task;
import demo.service.TaskQueueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskQueueService taskQueueService;

    public TaskController(TaskQueueService taskQueueService) {
        this.taskQueueService = taskQueueService;
    }

    @PostMapping("/delete-topic/{topicId}")
    public ResponseEntity<String> enqueueDeleteTopic(@PathVariable Long topicId) {
        String taskId = taskQueueService.enqueueDeleteTopicTask(topicId);
        return ResponseEntity.ok("Tâche en file d’attente : " + taskId);
    }

    @GetMapping("/status/{taskId}")
    public ResponseEntity<Task> getStatus(@PathVariable String taskId) {
        Task task = taskQueueService.getTaskStatus(taskId);
        if (task == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(task);
    }
}