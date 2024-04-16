package freelanceplatform.data;

import freelanceplatform.model.Feedback;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedBackRepository extends CrudRepository<Feedback, Long> {
}
