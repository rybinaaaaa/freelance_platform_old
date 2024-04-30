package freelanceplatform.dto.entityDTO;

import lombok.Value;

@Value
public class FeedbackDTO {
    Integer id;
    UserDTO sender;
    UserDTO receiver;
    Integer rating;
    String comment;
}
