package demo.model;

public class Task {
    private String id;
    private String type;
    private String status;
    private Long topicId;

    public Task() {
    }

    public Task(String id, String type, String status, Long topicId) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.topicId = topicId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", topicId=" + topicId +
                '}';
    }
}