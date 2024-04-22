package freelanceplatform.dto.entityDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {

    private final String username;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final int rating;

}
