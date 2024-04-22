package freelanceplatform.dto.entityDTO;

import freelanceplatform.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationToFreelancersDTO implements NotificationDTO {

    private final String taskTitle;
    private final User taskOwner;
    private final String text;

}
