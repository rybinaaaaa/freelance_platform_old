package freelanceplatform.dto.entityCreationDTO;

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

}
