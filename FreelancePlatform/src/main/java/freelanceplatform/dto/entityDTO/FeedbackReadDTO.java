package freelanceplatform.dto.entityDTO;

public record FeedbackReadDTO(Integer id, UserDTO sender, UserDTO receiver, Integer rating, String comment) {
}
