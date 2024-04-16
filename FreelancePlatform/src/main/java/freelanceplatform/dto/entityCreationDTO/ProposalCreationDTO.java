package freelanceplatform.dto.entityCreationDTO;


import freelanceplatform.model.Freelancer;
import freelanceplatform.model.Task;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProposalCreationDTO {

    private final Freelancer freelancer;
    private final Task task;
}
