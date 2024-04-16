package freelanceplatform.dto.entityCreationDTO;

import freelanceplatform.model.NotificationType;
import freelanceplatform.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class NotificationCreationDTO {

    private NotificationType type;
    private Task task;
    private String text;

}
