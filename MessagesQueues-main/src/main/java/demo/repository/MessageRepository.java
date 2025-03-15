package com.example.repository;

import com.example.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // 🚀 Ajout d'une requête pour rechercher un message par contenu
    List<Message> findByContentContaining(String content);
}