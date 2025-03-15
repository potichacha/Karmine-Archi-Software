package demo.repository;

import demo.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // 🚀 Ajout d'une requête pour rechercher un message par contenu
    List<Message> findByContentContaining(String content);
}