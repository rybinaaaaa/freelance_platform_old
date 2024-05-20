package freelanceplatform.dto;

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

@Component
@RequiredArgsConstructor
public class Mapper {

    private final UserService userService;
    private final TaskService taskService;

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

    public User userDTOToUser(UserCreationDTO userDTO) {
        return User.builder()
                .username(userDTO.getUsername())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .role(userDTO.getRole())
                .build();
//        return new User(userDTO.getUsername(), userDTO.getFirstName(),
//                userDTO.getLastName(), userDTO.getEmail(), userDTO.getPassword(), userDTO.getRole());
    }

    public ProposalDTO proposalToProposalDTO(Proposal proposal) {
        return new ProposalDTO(
                proposal.getId(),
                Optional.ofNullable(proposal.getFreelancer())
                        .map(this::userToDTO)
                        .orElse(null),
                Optional.ofNullable(proposal.getTask())
                        .map(this::taskToTaskDTO)
                        .orElse(null));
    }

    public Proposal proposalDTOToProposal(ProposalDTO proposalDTO) {
        return new Proposal(
                proposalDTO.getId(),
                Optional.ofNullable(proposalDTO.getFreelancer())
                        .map(UserDTO::getId)
                        .map(userService::find)
                        .orElse(null),
                Optional.ofNullable(proposalDTO.getTask())
                        .map(TaskDTO::getId)
                        .map(taskService::getById)
                        .orElse(null)
        );
    }

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

    public Feedback feedbackDtoToFeedback(FeedbackDTO fb) {
        Feedback feedback = new Feedback();
        feedback.setId(fb.getId());
        feedback.setRating(fb.getRating());
        feedback.setComment(fb.getComment());

        User receiver = Optional.ofNullable(fb.getReceiver())
                .map(UserDTO::getId)
                .map(userService::find)
                .orElse(null);

        User sender = Optional.ofNullable(fb.getSender())
                .map(UserDTO::getId)
                .map(userService::find)
                .orElse(null);

        feedback.setReceiver(receiver);
        feedback.setSender(sender);

        return feedback;
    }

    public FeedbackDTO feedbackToFeedbackDto(Feedback fb) {
        return new FeedbackDTO(
                fb.getId(),
                userToDTO(fb.getSender()),
                userToDTO(fb.getReceiver()),
                fb.getRating(),
                fb.getComment()
        );
    }
}
