package freelanceplatform.dto.entityDTO;

import freelanceplatform.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Integer id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private int rating;
    private Role role;
}
