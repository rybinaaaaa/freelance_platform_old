package freelanceplatform.dto.entityCreationDTO;

import freelanceplatform.model.Task;
import freelanceplatform.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

@Value
public class ProposalCreationDTO {

    Integer freelancerId;
    Integer taskId;
}

