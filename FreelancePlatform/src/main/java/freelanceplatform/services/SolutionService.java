package freelanceplatform.services;

import freelanceplatform.data.SolutionRepository;
import freelanceplatform.data.TaskRepository;
import freelanceplatform.exceptions.NotFoundException;
import freelanceplatform.model.Solution;
import freelanceplatform.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
public class SolutionService {

    private final SolutionRepository solutionRepo;
    private final TaskRepository taskRepo;

    @Autowired
    public SolutionService(SolutionRepository solutionRepo, TaskRepository taskRepo) {
        this.solutionRepo = solutionRepo;
        this.taskRepo = taskRepo;
    }

    @Transactional
    public void save(Solution solution){
        Objects.requireNonNull(solution);
        solutionRepo.save(solution);
    }

    @Transactional(readOnly = true)
    public Solution getById(Integer id){
        Objects.requireNonNull(id);
        Optional<Solution> solution = solutionRepo.findById(id);
        if (solution.isEmpty()) throw new NotFoundException("Solution identified by " + id + " not found.");
        return solution.get();
    }

    public Solution getByTask(Task task){
        Objects.requireNonNull(task);
        Optional<Solution> solution = solutionRepo.findByTask(task);
        if (solution.isEmpty()) throw new NotFoundException("Solution identified by task" + task.getId() + " not found.");
        return solution.get();
    }

    public boolean exists(Integer id){
        Objects.requireNonNull(id);
        return solutionRepo.existsById(id);
    }

    @Transactional
    public void update(Solution solution){
        Objects.requireNonNull(solution);
        if (exists(solution.getId())){
            solutionRepo.save(solution);
        } else {
            throw new NotFoundException("Solution to update identified by " + solution.getId() + " not found.");
        }
    }

    @Transactional
    public void delete(Solution solution){
        Objects.requireNonNull(solution);
        if (exists(solution.getId())){
            solution.getTask().setSolution(null);
            taskRepo.save(solution.getTask());
            solutionRepo.delete(solution);
        } else {
            throw new NotFoundException("Solution to update identified by " + solution.getId() + " not found.");
        }
    }
}
