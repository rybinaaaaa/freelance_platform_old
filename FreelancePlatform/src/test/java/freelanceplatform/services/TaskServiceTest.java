package freelanceplatform.services;

import freelanceplatform.data.TaskRepository;
import freelanceplatform.data.UserRepository;
import freelanceplatform.environment.Generator;
import freelanceplatform.exceptions.NotFoundException;
import freelanceplatform.exceptions.ValidationException;
import freelanceplatform.model.Role;
import freelanceplatform.model.Task;
import freelanceplatform.model.TaskStatus;
import freelanceplatform.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TaskRepository taskRepo;

    private Task task;

    private User customer;

    private User freelancer;

    @BeforeEach
    public void setUp(){
        task = Generator.generateTask();
        customer = Generator.generateUser();
        customer.setRole(Role.USER);
        customer.addTaskToPosted(task);
        task.setCustomer(customer);
        freelancer = Generator.generateUser();
        freelancer.setRole(Role.USER);
        task.setFreelancer(freelancer);
        userRepo.save(customer);
        userRepo.save(freelancer);
        taskRepo.save(task);
    }

    @Test
    public void getThrowsNotFoundExceptionIfIdIsWrong(){
        assertThrows(NotFoundException.class, () -> taskService.get(-1));
    }

    @Test
    public void updateThrowsValidationExceptionIfTaskStatusIsNotUnassigned(){
        task.setStatus(TaskStatus.SUBMITTED);
        taskRepo.save(task);
        assertThrows(ValidationException.class, () -> taskService.update(task));
    }

    @Test
    public void updateThrowsNotFoundExceptionIfTaskDoesNotExist(){
        task = Generator.generateTask();
        task.setId(-1);
        assertThrows(NotFoundException.class, () -> taskService.update(task));
    }

    @Test
    public void deleteRemovesTaskFromFreelancersTakenTasks(){
        task.setStatus(TaskStatus.ASSIGNED);
        task.setFreelancer(freelancer);
        freelancer.addTaskToTaken(task);
        userRepo.save(freelancer);
        taskRepo.save(task);
        assertTrue(freelancer.getTakenTasks().contains(task));
        taskService.delete(task);
        assertFalse(freelancer.getTakenTasks().contains(task));
    }

    @Test
    public void deleteRemovesTaskFromCustomersPostedTasks(){
        task.setStatus(TaskStatus.ASSIGNED);
        assertTrue(customer.getPostedTasks().contains(task));
        taskService.delete(task);
        assertFalse(customer.getPostedTasks().contains(task));
    }

    @Test
    public void deleteThrowsNotFoundExceptionIfTaskDoesNotExist(){
        task = Generator.generateTask();
        task.setId(-1);
        assertThrows(NotFoundException.class, ()-> taskService.delete(task));
    }

    @Test
    public void assignAddsTaskToFreelancersTakenTasks(){
        taskService.assignFreelancer(task, freelancer);
        assertTrue(freelancer.getTakenTasks().contains(task));
    }

    @Test
    public void removeFreelancerRemovesTaskFromFreelancersTakenTasks(){
        task.setFreelancer(freelancer);
        task.setAssignedDate(LocalDateTime.now());
        freelancer.addTaskToTaken(task);
        taskRepo.save(task);
        userRepo.save(freelancer);
        taskService.removeFreelancer(task);
        assertFalse(freelancer.getTakenTasks().contains(task));
    }

}
