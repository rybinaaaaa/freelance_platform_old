package freelanceplatform.dto.entityCreationDTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCreationDTO {

    private final String username;
    private final String email;
    private final String password;

}
