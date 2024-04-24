package freelanceplatform.dto.entityDTO;


import freelanceplatform.model.TaskType;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class PostedTaskDTO {

    private final String customerName;
    private final String title;
    private final String problem;
    private final LocalDateTime deadline;
    private final Double payment;
    private final TaskType type;
}
