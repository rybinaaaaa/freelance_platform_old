package freelanceplatform.dto.entityCreationDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import freelanceplatform.model.Role;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;

@Data
@AllArgsConstructor
public class UserCreationDTO {

    private final String username;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
    private final Role role;

    @JsonIgnore
    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
}
