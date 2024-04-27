package freelanceplatform.dto;

import freelanceplatform.dto.entityCreationDTO.PostedTaskCreationDTO;
import freelanceplatform.dto.entityCreationDTO.ProposalCreationDTO;
import freelanceplatform.dto.entityCreationDTO.UserCreationDTO;
import freelanceplatform.dto.entityDTO.*;
import freelanceplatform.model.Proposal;
import freelanceplatform.model.Task;
import freelanceplatform.model.User;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    public UserDTO userToDTO(User user){
        return new UserDTO(user.getUsername(), user.getFirstName(), user.getLastName(),
                user.getEmail(), user.getRating(), user.getRole());
    }

    public User userDTOToUser(UserCreationDTO userDTO){
        return new User(userDTO.getUsername(), userDTO.getFirstName(),
                userDTO.getLastName(), userDTO.getEmail(), userDTO.getPassword(), userDTO.getRole());
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
                task.getPayment(),
                task.getType());
    }

    public Task postedTaskDTOToTask(PostedTaskCreationDTO taskDTO){
        return new Task(
                taskDTO.getCustomer(),
                taskDTO.getTitle(),
                taskDTO.getProblem(),
                taskDTO.getDeadline(),
                taskDTO.getPayment(),
                taskDTO.getType());
    }
}
