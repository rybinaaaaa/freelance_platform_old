package freelanceplatform.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import freelanceplatform.dto.entityCreationDTO.TaskCreationDTO;
import freelanceplatform.dto.entityCreationDTO.UserCreationDTO;
import freelanceplatform.dto.entityDTO.*;
import freelanceplatform.model.Feedback;
import freelanceplatform.model.Proposal;
import freelanceplatform.model.Task;
import freelanceplatform.model.User;
import freelanceplatform.services.TaskService;
import freelanceplatform.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Mapper class for converting between entities and DTOs.
 * This class provides methods to map User, Proposal, Task, and Feedback entities to their corresponding DTOs and vice versa.
 */
@Component
@RequiredArgsConstructor
public class Mapper {

    private final UserService userService;
    private final TaskService taskService;
    private final ObjectMapper objectMapper;

    /**
     * Converts a User entity to a UserDTO.
     *
     * @param user the User entity to convert
     * @return the converted UserDTO
     */
    public UserDTO userToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRating(),
                user.getRole());
    }

    /**
     * Converts a UserCreationDTO to a User entity.
     *
     * @param userDTO the UserCreationDTO to convert
     * @return the converted User entity
     */
    public User userDTOToUser(UserCreationDTO userDTO) {
        return User.builder()
                .username(userDTO.getUsername())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .role(userDTO.getRole())
                .build();
    }

    /**
     * Converts user to json
     * @param user to convert
     * @return json string
     */
    public String convertUserToJson(User user) {
        try {
            return objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting User to JSON", e);
        }
    }

    /**
     * Converts a Proposal entity to a ProposalDTO.
     *
     * @param proposal the Proposal entity to convert
     * @return the converted ProposalDTO
     */
    public ProposalDTO proposalToProposalDTO(Proposal proposal) {
        return new ProposalDTO(
                proposal.getId(),
                Optional.ofNullable(proposal.getFreelancer())
                        .map(User::getId)
                        .orElse(null),
                Optional.ofNullable(proposal.getTask())
                        .map(Task::getId)
                        .orElse(null));
    }

    /**
     * Converts a ProposalDTO to a Proposal entity.
     *
     * @param proposalDTO the ProposalDTO to convert
     * @return the converted Proposal entity
     */
    public Proposal proposalDTOToProposal(ProposalDTO proposalDTO) {
        return new Proposal(
                proposalDTO.getId(),
                Optional.ofNullable(proposalDTO.getFreelancerId())
                        .map(userService::find)
                        .orElse(null),
                Optional.ofNullable(proposalDTO.getTaskId())
                        .map(taskService::getById)
                        .orElse(null)
        );
    }

    /**
     * Converts a Task entity to a TaskDTO.
     *
     * @param task the Task entity to convert
     * @return the converted TaskDTO
     */
    public TaskDTO taskToTaskDTO(Task task) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(task.getId());
        taskDTO.setCustomerName(task.getCustomer().getFirstName());
        if (task.getFreelancer() != null) {
            taskDTO.setFreelancerName(task.getFreelancer().getFirstName());
        }
        taskDTO.setTitle(task.getTitle());
        taskDTO.setProblem(task.getProblem());
        taskDTO.setDeadline(task.getDeadline());
        taskDTO.setStatus(task.getStatus());
        taskDTO.setType(task.getType());
        taskDTO.setPayment(task.getPayment());
        return taskDTO;
    }

    /**
     * Converts a TaskCreationDTO to a Task entity.
     *
     * @param taskDTO the TaskCreationDTO to convert
     * @return the converted Task entity
     */
    public Task taskDTOToTask(TaskCreationDTO taskDTO) {
        Task task = new Task(
//                taskDTO.getCustomer(),
                taskDTO.getTitle(),
                taskDTO.getProblem(),
                taskDTO.getDeadline(),
                taskDTO.getPayment(),
                taskDTO.getType());
//        task.setId(taskDTO.getId());
        task.setStatus(taskDTO.getTaskStatus());
        return task;
    }

    /**
     * Converts a FeedbackDTO to a Feedback entity.
     *
     * @param fb the FeedbackDTO to convert
     * @return the converted Feedback entity
     */
    public Feedback feedbackDtoToFeedback(FeedbackDTO fb) {
        Feedback feedback = new Feedback();
        feedback.setId(fb.getId());
        feedback.setRating(fb.getRating());
        feedback.setComment(fb.getComment());

        User receiver = Optional.ofNullable(fb.getReceiverId())
                .map(userService::find)
                .orElse(null);

        User sender = Optional.ofNullable(fb.getSenderId())
                .map(userService::find)
                .orElse(null);

        feedback.setReceiver(receiver);
        feedback.setSender(sender);

        return feedback;
    }

    /**
     * Converts a Feedback entity to a FeedbackDTO.
     *
     * @param fb the Feedback entity to convert
     * @return the converted FeedbackDTO
     */
    public FeedbackDTO feedbackToFeedbackDto(Feedback fb) {
        return new FeedbackDTO(
                fb.getId(),
                fb.getSender().getId(),
                fb.getReceiver().getId(),
                fb.getRating(),
                fb.getComment()
        );
    }
}
