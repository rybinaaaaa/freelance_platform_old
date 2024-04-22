package freelanceplatform.dto.entityCreationDTO;

import freelanceplatform.model.Task;
import freelanceplatform.model.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProposalCreationDTO {

    private final User freelancer;
    private final Task task;
}
