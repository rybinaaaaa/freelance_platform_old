package freelanceplatform.dto.entityCreationDTO;


import freelanceplatform.model.Customer;
import freelanceplatform.model.Freelancer;
import freelanceplatform.model.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class PostedTaskCreationDTO {

    private Customer customer;
    private Freelancer freelancer;
    private String title;
    private String problem;
    private Date deadline;
    private TaskStatus status;
    private Double payment;
}
