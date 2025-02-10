package demo.data;

import demo.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

@Repository
public interface TopicData extends JpaRepository<Topic, Long> {

    Optional<Topic> findByName(String name);  // ✅ Retourne un Optional pour éviter les nulls

    @Query("SELECT t FROM Topic t LEFT JOIN FETCH t.topicMessages WHERE t.id = :id")
    Optional<Topic> findByIdWithMessagesSorted(@Param("id") Long id);
}