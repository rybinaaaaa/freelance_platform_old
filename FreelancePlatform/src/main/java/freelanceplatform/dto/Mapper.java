package freelanceplatform.dto;

import freelanceplatform.dto.entityCreationDTO.PostedTaskCreationDTO;
import freelanceplatform.dto.entityCreationDTO.ProposalCreationDTO;
import freelanceplatform.dto.entityCreationDTO.UserCreationDTO;
import freelanceplatform.dto.entityDTO.*;
import freelanceplatform.model.Notification;
import freelanceplatform.model.Proposal;
import freelanceplatform.model.Task;
import freelanceplatform.model.User;

//will be used in controllers
public class Mapper {

    public UserDTO userToDTO(User user){
        return new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail(), user.getRating());
    }

    public User userDTOToUser(UserCreationDTO userDTO){
        return new User(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(), userDTO.getPassword());
    }

    public ProposalDTO proposalToDTO(Proposal proposal){
        return new ProposalDTO(proposal.getFreelancer().getFirstName(), proposal.getTask().getTitle());
    }

    public Proposal proposalDTOToProposal(ProposalCreationDTO proposalDTO){
        return new Proposal(proposalDTO.getFreelancer(), proposalDTO.getTask());
    }

    public PostedTaskDTO taskToPostedTaskDTO(Task task){
        return new PostedTaskDTO(
                task.getCustomer().getFirstName(),
                task.getTitle(),
                task.getProblem(),
                task.getDeadline(),
                task.getPayment());
    }

    public Task postedTaskDTOToTask(PostedTaskCreationDTO taskDTO){
        return new Task(
                taskDTO.getCustomer(),
                taskDTO.getFreelancer(),
                taskDTO.getTitle(),
                taskDTO.getProblem(),
                taskDTO.getDeadline(),
                taskDTO.getStatus(),
                taskDTO.getPayment());
    }

    public NotificationDTO notificationToNotificationDTO(Notification notification){
        return switch (notification.getType()) {
            case TaskWasCompleted -> new NotificationToCustomerDTO(
                    notification.getTask().getTitle(),
                    notification.getTask().getFreelancer());
            case TaskWasPosted -> new NotificationToFreelancersDTO(
                    notification.getTask().getTitle(),
                    notification.getTask().getCustomer(),
                    notification.getText());
        };
    }
}
