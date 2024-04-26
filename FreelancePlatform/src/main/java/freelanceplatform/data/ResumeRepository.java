package freelanceplatform.data;

import freelanceplatform.model.Resume;
import org.springframework.data.repository.CrudRepository;

public interface ResumeRepository extends CrudRepository<Resume, Integer> {
}
