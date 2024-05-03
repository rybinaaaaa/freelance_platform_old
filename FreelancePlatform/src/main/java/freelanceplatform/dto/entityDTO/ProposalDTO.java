package freelanceplatform.dto.entityDTO;

import lombok.Value;

@Value
public class ProposalDTO {

    Integer id;
    UserDTO freelancer;
    TaskDTO task;
}
