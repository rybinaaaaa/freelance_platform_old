package freelanceplatform.dto.entityCreationDTO;

import freelanceplatform.model.TaskType;
import freelanceplatform.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TaskCreationDTO {

    private User customer;
    private String title;
    private String problem;
    private LocalDateTime deadline;
    private Double payment;
    private TaskType type;
}