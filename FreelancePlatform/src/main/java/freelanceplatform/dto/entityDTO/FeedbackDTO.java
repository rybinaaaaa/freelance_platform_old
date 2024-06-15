package freelanceplatform.dto.entityDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data@AllArgsConstructor
public class FeedbackDTO {
    Integer id;
    Integer senderId;
    Integer receiverId;
    Integer rating;
    String comment;
}
