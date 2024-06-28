package freelanceplatform.data;


import freelanceplatform.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> getByUsername(String username);
}
