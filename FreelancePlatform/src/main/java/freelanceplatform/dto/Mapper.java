package freelanceplatform.dto;

import freelanceplatform.dto.entityCreationDTO.TaskCreationDTO;
import freelanceplatform.dto.entityCreationDTO.ProposalCreationDTO;
import freelanceplatform.dto.entityCreationDTO.UserCreationDTO;
import freelanceplatform.dto.entityDTO.*;
import freelanceplatform.model.Feedback;
import freelanceplatform.model.Proposal;
import freelanceplatform.model.Task;
import freelanceplatform.model.User;
import freelanceplatform.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class Mapper {

    private final UserService userService;

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

    public ProposalDTO proposalToDTO(Proposal proposal) {
        return new ProposalDTO(proposal.getFreelancer().getFirstName(), proposal.getTask().getTitle());
    }

    public Proposal proposalDTOToProposal(ProposalCreationDTO proposalDTO) {
        return new Proposal(proposalDTO.getFreelancer(), proposalDTO.getTask());
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
                taskDTO.getCustomer(),
                taskDTO.getTitle(),
                taskDTO.getProblem(),
                taskDTO.getDeadline(),
                taskDTO.getPayment(),
                taskDTO.getType());
        task.setId(taskDTO.getId());
        task.setStatus(taskDTO.getTaskStatus());
        return task;
    }

    public Feedback feedbackReadDTOToFeedback(FeedbackReadDTO fb) {
        Feedback feedback = new Feedback();
        feedback.setId(fb.id());
        feedback.setRating(fb.rating());
        feedback.setComment(fb.comment());

        User receiver = Optional.ofNullable(fb.receiver())
                .map(UserDTO::getId)
                .map(userService::find)
                .orElse(null);

        User sender = Optional.ofNullable(fb.sender())
                .map(UserDTO::getId)
                .map(userService::find)
                .orElse(null);

        feedback.setReceiver(receiver);
        feedback.setSender(sender);

        return feedback;
    }

    public FeedbackReadDTO feedbackToFeedbackReadDTO(Feedback fb) {
        return new FeedbackReadDTO(
                fb.getId(),
                userToDTO(fb.getSender()),
                userToDTO(fb.getReceiver()),
                fb.getRating(),
                fb.getComment()
        );
    }
}
