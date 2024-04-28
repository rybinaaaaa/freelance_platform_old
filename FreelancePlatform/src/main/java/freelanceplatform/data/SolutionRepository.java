package freelanceplatform.data;

import freelanceplatform.model.Solution;
import freelanceplatform.model.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SolutionRepository extends CrudRepository<Solution, Integer> {

    Optional<Solution> findByTask(Task task);

}
