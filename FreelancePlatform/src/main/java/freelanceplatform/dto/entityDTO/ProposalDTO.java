package freelanceplatform.dto.entityDTO;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProposalDTO {

    private final String freelancerName;
    private final String taskTitle;
}
