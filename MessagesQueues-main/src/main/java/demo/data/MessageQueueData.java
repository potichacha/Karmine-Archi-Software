package demo.data;

import demo.model.MessageQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageQueueData extends JpaRepository<MessageQueue, String> {}
