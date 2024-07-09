package freelanceplatform.dto.entityCreationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationDTO {

    private  String username;
    private  String firstName;
    private  String lastName;
    private  String email;
    private  String password;
//    private  Role role;
//
//    @JsonIgnore
//    public boolean isAdmin() {
//        return role == Role.ADMIN;
//    }
}
