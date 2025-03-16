package demo.repository;

import demo.model.MessageQueue;
import demo.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageQueueRepository extends JpaRepository<MessageQueue, Long> {
    Optional<MessageQueue> findByTopic(Topic topic);
}
