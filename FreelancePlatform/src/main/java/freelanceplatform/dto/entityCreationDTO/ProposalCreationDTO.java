package freelanceplatform.dto.entityCreationDTO;

import freelanceplatform.model.Task;
import freelanceplatform.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProposalCreationDTO {

    private final User freelancer;
    private final Task task;
}
