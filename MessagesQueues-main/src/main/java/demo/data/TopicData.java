package demo.data;

import demo.model.Topic;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface TopicData extends JpaRepository<Topic, Long> {

    Topic findByName(String name);

    @Query("SELECT t FROM Topic t LEFT JOIN FETCH t.topicMessages tm WHERE t.id = :id ORDER BY tm.messageNumber ASC")
    Topic findByIdWithMessagesSorted(@Param("id") Long id);
}