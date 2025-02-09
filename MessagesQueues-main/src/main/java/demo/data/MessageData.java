package demo.data;

import demo.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageData extends JpaRepository<Message, Long> {

    // 🔹 Trouver les messages disponibles avant une certaine date
    List<Message> findByQueueIdAndAvailableAtBefore(String queueId, LocalDateTime now);

    // 🔹 Récupérer les messages avec un ID supérieur à une certaine valeur
    List<Message> findByIdGreaterThan(Long id);

    // 🔹 Rechercher les messages contenant un texte donné (insensible à la casse)
    @Query("SELECT m FROM Message m WHERE LOWER(m.content) LIKE LOWER(CONCAT('%', :content, '%'))")
    List<Message> searchByContent(@Param("content") String content);

    List<Message> findByContentContainingIgnoreCase(String keyword);
}