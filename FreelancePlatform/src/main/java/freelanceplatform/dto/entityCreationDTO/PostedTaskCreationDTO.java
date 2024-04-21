package freelanceplatform.dto.entityCreationDTO;

import freelanceplatform.model.TaskStatus;
import freelanceplatform.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
public class PostedTaskCreationDTO {

    private User customer;
    private String title;
    private String problem;
    private LocalDateTime deadline;
    private Double payment;
}
