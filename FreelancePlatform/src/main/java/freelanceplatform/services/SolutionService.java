package freelanceplatform.services;

import freelanceplatform.data.SolutionRepository;
import freelanceplatform.data.TaskRepository;
import freelanceplatform.exceptions.NotFoundException;
import freelanceplatform.model.Solution;
import freelanceplatform.model.Task;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = {"solutions"})
public class SolutionService {

    private final SolutionRepository solutionRepo;
    private final TaskRepository taskRepo;

    @Autowired
    public SolutionService(SolutionRepository solutionRepo, TaskRepository taskRepo) {
        this.solutionRepo = solutionRepo;
        this.taskRepo = taskRepo;
    }

    /**
     * Saves a new solution.
     *
     * @param solution Solution object to be saved.
     */
    @CachePut(key = "#solution.id")
    @Transactional
    public Solution save(Solution solution) {
        Objects.requireNonNull(solution);

        Task task = solution.getTask();
        Objects.requireNonNull(task);

        solution.setTask(taskRepo.findById(task.getId()).orElse(null));

        solutionRepo.save(solution);
        return solution;
    }


    /**
     * Retrieves a solution by its ID.
     *
     * @param id ID of the solution to retrieve.
     * @return Solution object if found.
     * @throws NotFoundException if the solution with the specified ID is not found.
     */
    @Transactional(readOnly = true)
    @Cacheable
    public Solution getById(Integer id) {
        Objects.requireNonNull(id);
        Optional<Solution> solution = solutionRepo.findById(id);
        if (solution.isEmpty()) throw new NotFoundException("Solution identified by " + id + " not found.");
        return solution.get();
    }

    /**
     * Retrieves a solution associated with a specific task.
     *
     * @param task Task object to retrieve the associated solution.
     * @return Solution object associated with the task.
     * @throws NotFoundException if no solution is found for the specified task.
     */
    @Transactional(readOnly = true)
    @Cacheable
    public Solution getByTask(Task task) {
        Objects.requireNonNull(task);
        Optional<Solution> solution = solutionRepo.findByTask(task);
        if (solution.isEmpty())
            throw new NotFoundException("Solution identified by task" + task.getId() + " not found.");
        return solution.get();
    }

    /**
     * Checks if a solution with the given ID exists.
     *
     * @param id ID of the solution to check.
     * @return true if a solution with the specified ID exists; false otherwise.
     */
    public boolean exists(Integer id) {
        Objects.requireNonNull(id);
        return solutionRepo.existsById(id);
    }

    /**
     * Updates details of an existing solution.
     *
     * @param solution Updated Solution object.
     * @throws NotFoundException if the solution to update is not found.
     */
    @Transactional
    @CachePut(key = "#solution.id")
    public Solution update(Solution solution) {
        Objects.requireNonNull(solution);
        if (exists(solution.getId())) {
            solutionRepo.save(solution);
            return solution;
        } else {
            throw new NotFoundException("Solution to update identified by " + solution.getId() + " not found.");
        }
    }

    /**
     * Deletes a solution.
     *
     * @param solution Solution object to delete.
     * @throws NotFoundException if the solution to delete is not found.
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "#solution.id"),
            @CacheEvict(value = "tasks", condition = "#solution.task != null", key = "#solution.task.id")
    })
    public void delete(Solution solution) {
        Objects.requireNonNull(solution);

        if (exists(solution.getId())) {

            Optional.ofNullable(solution.getTask())
                    .ifPresent(task -> task.setSolution(null));

            solutionRepo.delete(solution);
        } else {
            throw new NotFoundException("Solution to update identified by " + solution.getId() + " not found.");
        }
    }
}
