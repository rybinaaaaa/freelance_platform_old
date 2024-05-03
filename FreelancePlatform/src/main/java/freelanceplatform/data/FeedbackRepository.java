package freelanceplatform.data;

import freelanceplatform.model.Feedback;
import freelanceplatform.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends CrudRepository<Feedback, Integer> {

    List<Feedback> findAll();

    List<Feedback> findByReceiver(User receiver);

    List<Feedback> findBySender(User sender);
}
