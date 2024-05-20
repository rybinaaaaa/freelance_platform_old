package freelanceplatform.dto.entityCreationDTO;

import freelanceplatform.model.TaskStatus;
import freelanceplatform.model.TaskType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TaskCreationDTO {

//    private final Integer id;
//    private User customer;
    private String title;
    private String problem;
    private LocalDateTime deadline;
    private TaskStatus taskStatus;
    private Double payment;
    private TaskType type;
}