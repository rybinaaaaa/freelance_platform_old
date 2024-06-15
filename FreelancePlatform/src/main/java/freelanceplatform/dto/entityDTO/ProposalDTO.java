package freelanceplatform.dto.entityDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

@Data
@AllArgsConstructor
public class ProposalDTO {

    Integer id;
    Integer freelancerId;
    Integer taskId;
}
