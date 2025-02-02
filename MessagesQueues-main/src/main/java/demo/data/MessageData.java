package com.example.karmine.data;

import com.example.karmine.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageData extends JpaRepository<Message, Long> {
    List<Message> findByQueueIdAndAvailableAtBefore(String queueId, LocalDateTime now);
}
