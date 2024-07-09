package freelanceplatform.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import freelanceplatform.data.SolutionRepository;
import freelanceplatform.data.TaskRepository;
import freelanceplatform.data.UserRepository;
import freelanceplatform.exceptions.NotFoundException;
import freelanceplatform.exceptions.ValidationException;
import freelanceplatform.kafka.ChangesProducer;
import freelanceplatform.kafka.topics.TaskChangesTopic;
import freelanceplatform.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static freelanceplatform.kafka.topics.TaskChangesTopic.*;


@Service
@CacheConfig(cacheNames={"tasks"})
@Slf4j
public class TaskService {

    private final TaskRepository taskRepo;
    private final UserRepository userRepo;
    private final SolutionRepository solutionRepo;
    private final ChangesProducer<TaskChangesTopic> taskChangesProducer;

    @Autowired
    public TaskService(TaskRepository taskRepo, UserRepository userRepo, SolutionRepository solutionRepo, ChangesProducer<TaskChangesTopic> taskChangesProducer) {
        this.taskRepo = taskRepo;
        this.userRepo = userRepo;
        this.solutionRepo = solutionRepo;
        this.taskChangesProducer = taskChangesProducer;
    }

    /**
     * Saves a new task.
     *
     * @param task Task object to be saved.
     */
    @CachePut(key = "#task.id")
    @Transactional
    public Task save(Task task){
        Objects.requireNonNull(task);
        taskRepo.save(task);
        taskChangesProducer.sendMessage(taskChangesProducer.toJsonString(task), TaskPosted);
        return task;
    }

    /**
     * Saves a list of tasks.
     *
     * @param tasks List of Task objects to be saved.
     */
    @Transactional
    public void saveAll(List<Task> tasks){
        Objects.requireNonNull(tasks);
        taskRepo.saveAll(tasks);
    }

    /**
     * Retrieves a task by its ID.
     *
     * @param id ID of the task to retrieve.
     * @return Task object if found.
     * @throws NotFoundException if the task with the specified ID is not found.
     */
    @Transactional(readOnly = true)
    @Cacheable
    public Task getById(Integer id){
        log.info("Get task by id {}.", id);
        Objects.requireNonNull(id);
        Optional<Task> task = taskRepo.findById(id);
        if (task.isEmpty()) throw new NotFoundException("Task identified by " + id + " not found.");
        return task.get();
    }

    /**
     * Retrieves all unassigned tasks sorted by posted date.
     *
     * @param fromNewest Whether to sort tasks from newest to oldest.
     * @return Iterable of Task objects.
     */
    @Transactional(readOnly = true)
    public Iterable<Task> getAllTaskBoardByPostedDate(boolean fromNewest){
        if (fromNewest) {
            return taskRepo.findAllByStatusFromNewest(TaskStatus.UNASSIGNED);
        } else {
            return taskRepo.findAllByStatusFromOldest(TaskStatus.UNASSIGNED);
        }
    }

    /**
     * Retrieves all unassigned tasks of a specific type sorted by posted date.
     *
     * @param type       TaskType to filter tasks by.
     * @param fromNewest Whether to sort tasks from newest to oldest.
     * @return Iterable of Task objects.
     */
    @Transactional(readOnly = true)
    public Iterable<Task> getAllTaskBoardByTypeAndPostedDate(TaskType type, boolean fromNewest) {
        if (fromNewest) {
            return taskRepo.findAllByTypeAndStatusFromNewest(type, TaskStatus.UNASSIGNED);
        } else {
            return taskRepo.findAllByTypeAndStatusFromOldest(type, TaskStatus.UNASSIGNED);
        }
    }

    /**
     * Retrieves all tasks taken by a user based on deadline status.
     *
     * @param userId ID of the user (freelancer) who took the tasks.
     * @param expired Whether to include expired tasks.
     * @return Iterable of Task objects.
     */
    @Transactional(readOnly = true)
    public Iterable<Task> getAllTakenByUserIdAndDeadlineStatus(Integer userId, boolean expired){
        if (expired){
            return taskRepo.findAllTakenByFreelancerIdDeadlineExpired(userId);
        } else {
            return taskRepo.findAllTakenByFreelancerIdDeadlineNotExpired(userId);
        }
    }

    /**
     * Retrieves all tasks taken by a user based on status and deadline status.
     *
     * @param userId     ID of the user (freelancer) who took the tasks.
     * @param taskStatus TaskStatus to filter tasks by.
     * @param expired    Whether to include expired tasks.
     * @return Iterable of Task objects.
     */
    @Transactional(readOnly = true)
    public Iterable<Task> getAllTakenByUserIdAndStatusAndDeadlineStatus(Integer userId, TaskStatus taskStatus, boolean expired){
        if (expired){
            return taskRepo.findAllTakenByFreelancerIdAndStatusDeadlineExpired(userId, taskStatus);
        } else {
            return taskRepo.findAllTakenByFreelancerIdAndStatusDeadlineNotExpired(userId, taskStatus);
        }
    }

    /**
     * Retrieves all tasks posted by a user based on expiration status.
     *
     * @param userId  ID of the user (customer) who posted the tasks.
     * @param expired Whether to include expired tasks.
     * @return Iterable of Task objects.
     */
    @Transactional(readOnly = true)
    public Iterable<Task> getAllPostedByUserIdAndExpiredStatus(Integer userId, boolean expired){
        if (expired){
            return taskRepo.findAllPostedByCustomerIdDeadlineExpired(userId);
        } else {
            return taskRepo.findAllPostedByCustomerIdDeadlineNotExpired(userId);
        }
    }

    /**
     * Retrieves all tasks posted by a user based on status and expiration status.
     *
     * @param userId     ID of the user (customer) who posted the tasks.
     * @param taskStatus TaskStatus to filter tasks by.
     * @param expired    Whether to include expired tasks.
     * @return Iterable of Task objects.
     */
    @Transactional(readOnly = true)
    public Iterable<Task> getAllPostedByUserIdAndStatusAndExpiredStatus(Integer userId, TaskStatus taskStatus , boolean expired){
        if (expired){
            return taskRepo.findAllPostedByCustomerIdAndStatusDeadlineExpired(userId, taskStatus);
        } else {
            return taskRepo.findAllPostedByCustomerIdAndStatusDeadlineNotExpired(userId, taskStatus);
        }
    }

    @Transactional(readOnly = true)
    public List<Task> findAll(){
        return taskRepo.findAll();
    }

    /**
     * Checks if a task with the given ID exists.
     *
     * @param id ID of the task to check.
     * @return true if a task with the specified ID exists; false otherwise.
     */
    public boolean exists(Integer id){
        Objects.requireNonNull(id);
        return taskRepo.existsById(id);
    }

    /**
     * Updates details of an existing task.
     *
     * @param task Updated Task object.
     * @throws ValidationException if the task cannot be updated (e.g., not unassigned).
     * @throws NotFoundException  if the task to update is not found.
     */
    @Transactional
    @CachePut(key = "#task.id")
    public Task update(Task task){
        Objects.requireNonNull(task);
        if (exists(task.getId())) {
            if (!task.getStatus().equals(TaskStatus.UNASSIGNED))
                throw new ValidationException("Task can be updated only if it is unassigned");
            return taskRepo.save(task);
        } else {
            throw new NotFoundException("Task to update identified by " + task.getId() + " not found.");
        }
    }

    /**
     * Deletes a task.
     *
     * @param task Task object to delete.
     * @throws NotFoundException if the task to delete is not found.
     */
    @Transactional
    @CacheEvict(key = "#task.id")
    public void delete(Task task){
        Objects.requireNonNull(task);
        if (exists(task.getId())) {
            task.getCustomer().removePostedTask(task);
            if (task.getFreelancer()!=null) {
                task.getFreelancer().removeTakenTask(task);
                userRepo.save(task.getFreelancer());
            }
            userRepo.save(task.getCustomer());
            taskRepo.delete(task);
        } else {
            throw new NotFoundException("Task to delete identified by " + task.getId() + " not found.");
        }
    }

    /**
     * Assigns a freelancer to a task.
     *
     * @param task      Task object to assign a freelancer to.
     * @param freelancer User object representing the freelancer to assign.
     */
    @Transactional
    @CachePut(key = "#task.id")
    public Task assignFreelancer(Task task, User freelancer){
        Objects.requireNonNull(task);
        Objects.requireNonNull(freelancer);
        task.setStatus(TaskStatus.ASSIGNED);
        task.setFreelancer(freelancer);
        task.setAssignedDate(LocalDateTime.now());
        freelancer.addTaskToTaken(task);
        taskRepo.save(task);
        userRepo.save(freelancer);
        taskChangesProducer.sendMessage(taskChangesProducer.toJsonString(task), FreelancerAssigned);

        return task;
    }

    /**
     * Accepts a solution for a task.
     *
     * @param task Task object to accept a solution for.
     */
    @Transactional
    @CachePut(key = "#task.id")
    public Task accept(Task task){
        Objects.requireNonNull(task);
        Objects.requireNonNull(task.getSolution());
        task.setStatus(TaskStatus.ACCEPTED);
        taskRepo.save(task);
        taskChangesProducer.sendMessage(taskChangesProducer.toJsonString(task), TaskAccepted);

        return task;
    }

    /**
     * Removes a freelancer from a task.
     *
     * @param task Task object to remove the freelancer from.
     */
    @Transactional
    @CachePut(key = "#task.id")
    public Task removeFreelancer(Task task){
        final User freelancer = task.getFreelancer();
        Objects.requireNonNull(task);
        freelancer.removeTakenTask(task);
        userRepo.save(task.getFreelancer());
        taskChangesProducer.sendMessage(taskChangesProducer.toJsonString(task), FreelancerRemoved);
        task.setStatus(TaskStatus.UNASSIGNED);
        task.setFreelancer(null);
        task.setAssignedDate(null);
        task.setSubmittedDate(null);
        taskRepo.save(task);

        return task;
    }

    /**
     * Attaches a solution to a task.
     *
     * @param taskId     Task id to attach the solution to.
     * @param solution Solution object to attach.
     */
    @Transactional
    @CachePut(key = "#taskId")
    public Task attachSolution(Integer taskId, Solution solution){
        Task task = taskRepo.findById(taskId).get();

        Objects.requireNonNull(task);
        Objects.requireNonNull(solution);

        task.setSolution(solution);
        solution.setTask(task);

        taskRepo.save(task);
        solutionRepo.save(solution);

        return task;
    }

    /**
     * Sends a task for review.
     *
     * @param task Task object to send for review.
     */
    @Transactional
    @CachePut(key = "#task.id")
    public Task senOnReview(Task task){
        task.setStatus(TaskStatus.SUBMITTED);
        task.setSubmittedDate(LocalDateTime.now());
        taskRepo.save(task);
        taskChangesProducer.sendMessage(taskChangesProducer.toJsonString(task), TaskSendOnReview);

        return task;
    }
}
