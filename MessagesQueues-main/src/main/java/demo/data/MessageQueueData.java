package com.example.karmine.data;

import com.example.karmine.model.MessageQueue;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface MessageQueueData extends JpaRepository<MessageQueue, String> {}
