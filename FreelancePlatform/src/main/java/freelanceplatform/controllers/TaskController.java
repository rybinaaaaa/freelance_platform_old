package freelanceplatform.controllers;


import freelanceplatform.model.*;
import freelanceplatform.security.model.UserDetails;
import freelanceplatform.services.TaskService;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/rest/tasks")
@PreAuthorize("permitAll()")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    public void create(){

    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Task getById(@PathVariable Integer id){
        return null;
    }

    //TASK BOARD
    public Iterable<Task> getAllTaskBoard(boolean fromNewest, @Nullable TaskType type){
        if (type == null) {
            return taskService.getAllTaskBoardByPostedDate(fromNewest);
        } else {
            return taskService.getAllTaskBoardByTypeAndPostedDate(type, fromNewest);
        }
    }

    //TAKEN TASKS
    public Iterable<Task> getAllTakenByTaskStatusAndExpiredStatus(@Nullable TaskStatus taskStatus, boolean expired, Authentication auth){
        final User user = ((UserDetails) auth.getPrincipal()).getUser();
        if (taskStatus == null) {
            return taskService.getAllTakenByUserIdAndDeadlineStatus(user.getId(), expired);
        } else {
            return taskService.getAllTakenByUserIdAndStatusAndDeadlineStatus(user.getId(), taskStatus, expired);
        }
    }

    //POSTED TASKS
    public Iterable<Task> getAllPostedByTaskStatusAndExpiredStatus(@Nullable TaskStatus taskStatus, boolean expired, Authentication auth){
        final User user = ((UserDetails) auth.getPrincipal()).getUser();
        if (taskStatus == null) {
            return taskService.getAllPostedByUserIdAndExpiredStatus(user.getId(), expired);
        } else {
            return taskService.getAllPostedByUserIdAndStatusAndExpiredStatus(user.getId(), taskStatus, expired);
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@PathVariable Integer id, @RequestParam Task updatedTask){

    }

    @DeleteMapping(value = "/{id}")
    public void delete(Task task){}

    public void assignFreelancer(Task task, User freelancer){}

    public void accept(Task task){}

    public void removeFreelancer(Task task){}

    public void sendOnReview(Task task, Solution solution){}


}
