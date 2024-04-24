package freelanceplatform.data;

import freelanceplatform.model.Task;
import freelanceplatform.model.TaskStatus;
import freelanceplatform.model.TaskType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends CrudRepository<Task, Integer> {

    Iterable<Task> findAllByStatus(TaskStatus status);

    Iterable<Task> findAllByTypeAndStatus(TaskType type, TaskStatus status);
}
