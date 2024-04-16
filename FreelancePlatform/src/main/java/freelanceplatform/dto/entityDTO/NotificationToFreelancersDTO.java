package freelanceplatform.dto.entityDTO;


import freelanceplatform.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationToFreelancersDTO implements NotificationDTO {

    private final String taskTitle;
    private final Customer taskOwner;
    private final String text;

}
