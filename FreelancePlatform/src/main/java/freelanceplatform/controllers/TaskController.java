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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;


@Slf4j
@RestController
@RequestMapping("/rest/tasks")
@PreAuthorize("permitAll()")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final Mapper mapper;

    @Autowired
    public TaskController(TaskService taskService, UserService userService, Mapper mapper) {
        this.taskService = taskService;
        this.userService = userService;
        this.mapper = mapper;
    }

    /**
     * Saves a new task based on the provided TaskCreationDTO.
     *
     * @param taskDTO the TaskCreationDTO object containing the task details
     * @param auth the Authentication object for the current user
     * @return ResponseEntity with the URI of the newly created task
     */
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

    /**
     * Retrieves a task by its ID.
     *
     * @param id the ID of the task
     * @return ResponseEntity with the task data or 404 if not found
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskDTO> getById(@PathVariable Integer id){
        try {
            final TaskDTO taskDTO = mapper.taskToTaskDTO(taskService.getById(id));
            return ResponseEntity.ok(taskDTO);
        } catch (NotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves all tasks based on specified filters.
     *
     * @param fromNewest Whether to sort tasks from newest to oldest.
     * @param type       Optional parameter to filter tasks by type.
     * @return ResponseEntity containing a list of TaskDTOs.
     */
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

    /**
     * Retrieves all tasks taken by the authenticated user based on status and expiration.
     *
     * @param taskStatus Optional parameter to filter tasks by status.
     * @param expired    Whether to include expired tasks.
     * @param auth       Authentication object containing user details.
     * @return ResponseEntity containing a list of TaskDTOs.
     */
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

    /**
     * Retrieves all tasks posted by the authenticated user based on status and expiration.
     *
     * @param taskStatus Optional parameter to filter tasks by status.
     * @param expired    Whether to include expired tasks.
     * @param auth       Authentication object containing user details.
     * @return ResponseEntity containing a list of TaskDTOs.
     */
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

    /**
     * Updates details of a posted task.
     *
     * @param id            ID of the task to update.
     * @param updatedTaskDTO Updated details of the task.
     * @return ResponseEntity indicating success or failure of the update operation.
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping(value = "/posted/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody TaskDTO updatedTaskDTO, Authentication auth){
        try {
            final Task task = taskService.getById(id);
            if (!hasAccess(task, auth))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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

    /**
     * Deletes a posted task.
     *
     * @param id ID of the task to delete.
     * @return ResponseEntity indicating success or failure of the delete operation.
     */
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @DeleteMapping(value = "/posted/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id, Authentication auth){
        try {
            final Task task = taskService.getById(id);
            if (!hasAccess(task, auth)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            taskService.delete(task);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Assigns a freelancer to a posted task.
     *
     * @param id         ID of the task to assign a freelancer to.
     * @param proposalId ID of the proposal from the freelancer.
     * @return ResponseEntity indicating success or failure of the assignment operation.
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/posted/{id}/proposals/{proposalId}")
    public ResponseEntity<Void> assignFreelancer(@PathVariable Integer id, @PathVariable Integer proposalId, Authentication auth){
        final Task task = taskService.getById(id);
        if (!hasAccess(task, auth)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        final User freelancer = userService.findFreelancerByProposalId(proposalId);
        taskService.assignFreelancer(task, freelancer);
        return ResponseEntity.noContent().build();
    }

    /**
     * Accepts a proposal for a posted task.
     *
     * @param id ID of the task to accept a proposal for.
     * @return ResponseEntity indicating success or failure of the acceptance operation.
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/posted/{id}/accept")
    public ResponseEntity<Void> accept(@PathVariable Integer id, Authentication auth){
        final Task task = taskService.getById(id);
        if (!hasAccess(task, auth)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        taskService.accept(task);
        return ResponseEntity.noContent().build();
    }

    /**
     * Removes a freelancer from a posted task.
     *
     * @param id ID of the task to remove a freelancer from.
     * @return ResponseEntity indicating success or failure of the removal operation.
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/{id}/remove-freelancer")
    public ResponseEntity<Void> removeFreelancer(@PathVariable Integer id, Authentication auth){
        final Task task = taskService.getById(id);
        if (!hasAccess(task, auth)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        taskService.removeFreelancer(task);
        return ResponseEntity.noContent().build();
    }

    /**
     * Attaches a solution to a taken task.
     *
     * @param id       ID of the task to attach a solution to.
     * @param solution Solution object containing the solution details.
     * @return ResponseEntity indicating success or failure of the attachment operation.
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/taken/{id}/attach-solution")
    public ResponseEntity<Void> attachSolution(@PathVariable Integer id, @RequestBody Solution solution){
        final Task task = taskService.getById(id);
        taskService.attachSolution(task, solution);
        return ResponseEntity.noContent().build();
    }

    /**
     * Sends a taken task for review.
     *
     * @param id ID of the task to send for review.
     * @return ResponseEntity indicating success or failure of sending the task for review.
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "/taken/{id}")
    public ResponseEntity<Void> sendOnReview(@PathVariable Integer id){
        taskService.senOnReview(taskService.getById(id));
        return ResponseEntity.noContent().build();
    }

    private boolean hasAccess(Task task, Authentication auth) {
        final User user = ((UserDetails) auth.getPrincipal()).getUser();
        return task.getCustomer().getId().equals(user.getId());
    }
}
