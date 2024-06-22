package freelanceplatform.dto.entityDTO;


import freelanceplatform.model.TaskStatus;
import freelanceplatform.model.TaskType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {

    private Integer id;
    private String customerUsername;
    private String freelancerUsername;
    private String title;
    private String problem;
    private LocalDateTime deadline;
    private Double payment;
    private TaskType type;
    private TaskStatus status;
}
