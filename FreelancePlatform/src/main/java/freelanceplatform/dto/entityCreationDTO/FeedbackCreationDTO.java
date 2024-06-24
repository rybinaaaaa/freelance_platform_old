package freelanceplatform.dto.entityCreationDTO;

import lombok.Value;

@Value
public class FeedbackCreationDTO {

    Integer senderId;
    Integer receiverId;
    Integer rating;
    String comment;
}
