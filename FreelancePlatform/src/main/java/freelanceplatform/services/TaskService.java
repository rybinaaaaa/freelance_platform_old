package freelanceplatform.services;


import freelanceplatform.data.SolutionRepository;
import freelanceplatform.data.TaskRepository;
import freelanceplatform.data.UserRepository;
import freelanceplatform.exceptions.NotFoundException;
import freelanceplatform.exceptions.ValidationException;
import freelanceplatform.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;


//todo добавить логику нотификации в методы
@Service
public class TaskService {

    private final TaskRepository taskRepo;
    private final UserRepository userRepo;
    private final SolutionRepository solutionRepo;

    @Autowired
    public TaskService(TaskRepository taskRepo, UserRepository userRepo, SolutionRepository solutionRepo) {
        this.taskRepo = taskRepo;
        this.userRepo = userRepo;
        this.solutionRepo = solutionRepo;
    }

    @Transactional
    public void save(Task task){
        Objects.requireNonNull(task);
        taskRepo.save(task);
    }

    @Transactional(readOnly = true)
    public Task getById(Integer id){
        Objects.requireNonNull(id);
        Optional<Task> task = taskRepo.findById(id);
        if (task.isEmpty()) throw new NotFoundException("Task identified by " + id + " not found.");
        return task.get();
    }


    //TASK BOARD
    @Transactional(readOnly = true)
    public Iterable<Task> getAllTaskBoardByPostedDate(boolean fromNewest){
        if (fromNewest) {
            return taskRepo.findAllByStatusFromNewest(TaskStatus.UNASSIGNED);
        } else {
            return taskRepo.findAllByStatusFromOldest(TaskStatus.UNASSIGNED);
        }
    }

    @Transactional(readOnly = true)
    public Iterable<Task> getAllTaskBoardByTypeAndPostedDate(TaskType type, boolean fromNewest) {
        if (fromNewest) {
            return taskRepo.findAllByTypeAndStatusFromNewest(type, TaskStatus.UNASSIGNED);
        } else {
            return taskRepo.findAllByTypeAndStatusFromOldest(type, TaskStatus.UNASSIGNED);
        }
    }

    //TAKEN TASKS
    @Transactional(readOnly = true)
    public Iterable<Task> getAllTakenByUserIdAndDeadlineStatus(Integer userId, boolean expired){
        if (expired){
            return taskRepo.findAllTakenByFreelancerIdDeadlineExpired(userId);
        } else {
            return taskRepo.findAllTakenByFreelancerIdDeadlineNotExpired(userId);
        }
    }

    @Transactional(readOnly = true)
    public Iterable<Task> getAllTakenByUserIdAndStatusAndDeadlineStatus(Integer userId, TaskStatus taskStatus, boolean expired){
        if (expired){
            return taskRepo.findAllTakenByFreelancerIdAndStatusDeadlineExpired(userId, taskStatus);
        } else {
            return taskRepo.findAllTakenByFreelancerIdAndStatusDeadlineNotExpired(userId, taskStatus);
        }
    }

    //POSTED TASKS
    @Transactional(readOnly = true)
    public Iterable<Task> getAllPostedByUserIdAndExpiredStatus(Integer userId, boolean expired){
        if (expired){
            return taskRepo.findAllPostedByCustomerIdDeadlineExpired(userId);
        } else {
            return taskRepo.findAllPostedByCustomerIdDeadlineNotExpired(userId);
        }
    }

    @Transactional(readOnly = true)
    public Iterable<Task> getAllPostedByUserIdAndStatusAndExpiredStatus(Integer userId, TaskStatus taskStatus , boolean expired){
        if (expired){
            return taskRepo.findAllPostedByCustomerIdAndStatusDeadlineExpired(userId, taskStatus);
        } else {
            return taskRepo.findAllPostedByCustomerIdAndStatusDeadlineNotExpired(userId, taskStatus);
        }
    }

    //OTHER
    public boolean exists(Integer id){
        Objects.requireNonNull(id);
        return taskRepo.existsById(id);
    }

    @Transactional
    public void update(Task task){
        Objects.requireNonNull(task);
        if (exists(task.getId())) {
            if (!task.getStatus().equals(TaskStatus.UNASSIGNED)) throw new ValidationException("Task can be updated only if it is unassigned");
            taskRepo.save(task);
        } else {
            throw new NotFoundException("Task to update identified by " + task.getId() + " not found.");
        }
    }

    @Transactional
    public void delete(Task task){
        Objects.requireNonNull(task);
        if (exists(task.getId())) {
            task.getCustomer().removePostedTask(task);
            if (task.getFreelancer()!=null) task.getFreelancer().removeTakenTask(task);
            userRepo.save(task.getCustomer());
            userRepo.save(task.getFreelancer());
            taskRepo.delete(task);
        } else {
            throw new NotFoundException("Task to delete identified by " + task.getId() + " not found.");
        }
    }

    @Transactional
    public void assignFreelancer(Task task, User freelancer){
        Objects.requireNonNull(task);
        Objects.requireNonNull(freelancer);
        task.setStatus(TaskStatus.ASSIGNED);
        task.setFreelancer(freelancer);
        task.setAssignedDate(LocalDateTime.now());
        freelancer.addTaskToTaken(task);
        taskRepo.save(task);
        userRepo.save(freelancer);
    }

    @Transactional
    public void accept(Task task){
        Objects.requireNonNull(task);
        task.setStatus(TaskStatus.ACCEPTED);
        taskRepo.save(task);
    }

    //todo должно быть реализовано в CorrectionService
//    @Transactional
//    public void returnWithCorrections(Task task){
//        Objects.requireNonNull(task);
//        task.setStatus(TaskStatus.ASSIGNED);
//        taskRepo.save(task);
//    }

    //todo мейби добавить ограничения
    @Transactional
    public void removeFreelancer(Task task){
        Objects.requireNonNull(task);
        task.getFreelancer().removeTakenTask(task);
        userRepo.save(task.getFreelancer());
        task.setStatus(TaskStatus.UNASSIGNED);
        task.setFreelancer(null);
        task.setAssignedDate(null);
        task.setSubmittedDate(null);
        taskRepo.save(task);
    }

    @Transactional
    public void attachSolution(Task task, Solution solution){
        Objects.requireNonNull(task);
        Objects.requireNonNull(solution);
        solution.setTask(task);
        task.setSolution(solution);
        taskRepo.save(task);
        solutionRepo.save(solution);
    }

    @Transactional
    public void senOnReview(Task task){
        task.setStatus(TaskStatus.SUBMITTED);
        task.setSubmittedDate(LocalDateTime.now());
        taskRepo.save(task);
    }
}
