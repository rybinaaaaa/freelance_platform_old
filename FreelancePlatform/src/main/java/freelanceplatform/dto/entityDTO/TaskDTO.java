package freelanceplatform.dto.entityDTO;


import freelanceplatform.model.TaskType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class TaskDTO {

    private final String customerName;
    private final String freelancerName;
    private final String title;
    private final String problem;
    private final LocalDateTime deadline;
    private final String payment;
    private final String type;
}
