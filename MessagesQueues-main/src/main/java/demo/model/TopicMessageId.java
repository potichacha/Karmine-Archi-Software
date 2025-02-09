package demo.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TopicMessageId implements Serializable {
    private Long topicId;
    private Long messageId;

    public TopicMessageId() {}

    public TopicMessageId(Long topicId, Long messageId) {
        this.topicId = topicId;
        this.messageId = messageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopicMessageId that = (TopicMessageId) o;
        return Objects.equals(topicId, that.topicId) &&
                Objects.equals(messageId, that.messageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topicId, messageId);
    }
}
