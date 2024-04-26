package freelanceplatform.services;

import freelanceplatform.data.SolutionRepository;
import freelanceplatform.exceptions.NotFoundException;
import freelanceplatform.model.Solution;
import freelanceplatform.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class SolutionService {

    private final SolutionRepository solutionRepo;

    @Autowired
    public SolutionService(SolutionRepository solutionRepo) {
        this.solutionRepo = solutionRepo;
    }

    public void save(Solution solution){
        Objects.requireNonNull(solution);
        solutionRepo.save(solution);
    }

    public Solution get(Integer id){
        Objects.requireNonNull(id);
        Optional<Solution> solution = solutionRepo.findById(id);
        if (solution.isEmpty()) throw new NotFoundException("Solution identified by " + id + " not found.");
        return solution.get();
    }

    public Solution getByVersion(String version){
        Objects.requireNonNull(version);
        Optional<Solution> solution = solutionRepo.getByVersion(version);
        if (solution.isEmpty()) throw new NotFoundException("Solution of version " + version + " not found.");
        return solution.get();
    }

    public Iterable<Solution> getAllByTask(Task task){
        Objects.requireNonNull(task);
        return null;
    }

    public boolean exists(Integer id){
        Objects.requireNonNull(id);
        return solutionRepo.existsById(id);
    }

    public void update(Solution solution){
        Objects.requireNonNull(solution);

    }

    public void delete(Solution solution){
        Objects.requireNonNull(solution);

    }
}
