package freelanceplatform.controllers;


import freelanceplatform.dto.Mapper;
import freelanceplatform.dto.entityCreationDTO.TaskCreationDTO;
import freelanceplatform.dto.entityDTO.TaskDTO;
import freelanceplatform.exceptions.NotFoundException;
import freelanceplatform.exceptions.ValidationException;
import freelanceplatform.model.*;
import freelanceplatform.security.model.UserDetails;
import freelanceplatform.services.ProposalService;
import freelanceplatform.services.SolutionService;
import freelanceplatform.services.TaskService;
import freelanceplatform.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/rest/tasks")
@PreAuthorize("permitAll()")
public class TaskController {

    private final TaskService taskService;
    private final SolutionService solutionService;
    private final ProposalService proposalService;
    private final UserService userService;
    private final Mapper mapper;

    @Autowired
    public TaskController(TaskService taskService, SolutionService solutionService, ProposalService proposalService, UserService userService, Mapper mapper) {
        this.taskService = taskService;
        this.solutionService = solutionService;
        this.proposalService = proposalService;
        this.userService = userService;
        this.mapper = mapper;
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> save(@RequestBody TaskCreationDTO taskDTO, Authentication auth){
        final User user = ((UserDetails) auth.getPrincipal()).getUser();
        final Task task = mapper.taskDTOToTask(taskDTO);
        task.setCustomer(user);
        taskService.save(task);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(task.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskDTO> getById(@PathVariable Integer id){
        try {
            final TaskDTO taskDTO = mapper.taskToTaskDTO(taskService.getById(id));
            return ResponseEntity.ok(taskDTO);
        } catch (NotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    //TASK BOARD
    @GetMapping(value = "/taskBoard", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<TaskDTO>> getAllTaskBoard(@RequestParam boolean fromNewest,
                                                             @RequestParam(required = false) TaskType type){
        final Iterable<Task> tasks;
        final List<TaskDTO> taskDTOs = new ArrayList<>();
        if (type == null) {
            tasks = taskService.getAllTaskBoardByPostedDate(fromNewest);
        } else {
            tasks = taskService.getAllTaskBoardByTypeAndPostedDate(type, fromNewest);
        }
        tasks.forEach(task -> taskDTOs.add(mapper.taskToTaskDTO(task)));
        return ResponseEntity.ok(taskDTOs);
    }

    //TAKEN TASKS
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value = "/taken", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<TaskDTO>> getAllTakenByTaskStatusAndExpiredStatus(@RequestParam(required = false) TaskStatus taskStatus,
                                                                                     @RequestParam boolean expired, Authentication auth){
        final User user = ((UserDetails) auth.getPrincipal()).getUser();
        final Iterable<Task> tasks;
        final List<TaskDTO> taskDTOs = new ArrayList<>();
        if (taskStatus == null) {
            tasks = taskService.getAllTakenByUserIdAndDeadlineStatus(user.getId(), expired);
        } else {
            tasks = taskService.getAllTakenByUserIdAndStatusAndDeadlineStatus(user.getId(), taskStatus, expired);
        }
        tasks.forEach(task -> taskDTOs.add(mapper.taskToTaskDTO(task)));
        return ResponseEntity.ok(taskDTOs);
    }

    //POSTED TASKS
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value = "/posted", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<TaskDTO>> getAllPostedByTaskStatusAndExpiredStatus(@RequestParam(required = false) TaskStatus taskStatus, @RequestParam boolean expired, Authentication auth){
        final User user = ((UserDetails) auth.getPrincipal()).getUser();
        final Iterable<Task> tasks;
        final List<TaskDTO> taskDTOs = new ArrayList<>();
        if (taskStatus == null) {
            tasks = taskService.getAllPostedByUserIdAndExpiredStatus(user.getId(), expired);
        } else {
            tasks = taskService.getAllPostedByUserIdAndStatusAndExpiredStatus(user.getId(), taskStatus, expired);
        }
        tasks.forEach(task -> taskDTOs.add(mapper.taskToTaskDTO(task)));
        return ResponseEntity.ok(taskDTOs);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping(value = "/posted/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody TaskDTO updatedTaskDTO){
        try {
            final Task task = taskService.getById(id);
            task.setTitle(updatedTaskDTO.getTitle());
            task.setProblem(updatedTaskDTO.getProblem());
            task.setDeadline(updatedTaskDTO.getDeadline());
            task.setType(updatedTaskDTO.getType());
            taskService.update(task);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ValidationException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @DeleteMapping(value = "/posted/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id){
        try {
            final Task task = taskService.getById(id);
            taskService.delete(task);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/posted/{id}/proposals/{proposalId}")
    public ResponseEntity<Void> assignFreelancer(@PathVariable Integer id, @PathVariable Integer proposalId){
        final Task task = taskService.getById(id);
        final User freelancer = userService.findFreelancerByProposalId(proposalId);
        taskService.assignFreelancer(task, freelancer);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/posted/{id}/accept")
    public ResponseEntity<Void> accept(@PathVariable Integer id){
        final Task task = taskService.getById(id);
        taskService.accept(task);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/{id}")
    public ResponseEntity<Void> removeFreelancer(@PathVariable Integer id){
        final Task task = taskService.getById(id);
        taskService.removeFreelancer(task);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/taken/{id}/attach-solution")
    public ResponseEntity<Void> attachSolution(@PathVariable Integer id, @RequestBody Solution solution){
        final Task task = taskService.getById(id);
        taskService.attachSolution(task, solution);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/taken/{id}")
    public ResponseEntity<Void> sendOnReview(@PathVariable Integer id){
        taskService.senOnReview(taskService.getById(id));
        return ResponseEntity.noContent().build();
    }
}
