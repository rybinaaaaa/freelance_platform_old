package freelanceplatform.dto.entityDTO;

import freelanceplatform.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {

    private final Integer id;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final int rating;
    private final Role role;
}
