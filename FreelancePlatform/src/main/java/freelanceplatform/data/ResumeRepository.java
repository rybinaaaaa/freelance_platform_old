package freelanceplatform.data;

import freelanceplatform.model.Resume;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ResumeRepository extends CrudRepository<Resume, Integer> {

    Optional<Resume> findByUserId(Integer id);
}
