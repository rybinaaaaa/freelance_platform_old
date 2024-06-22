package freelanceplatform.services.security;

import freelanceplatform.data.UserRepository;
import freelanceplatform.model.User;
import freelanceplatform.security.model.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("userDetailsService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepo;

    @Autowired
    public UserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Loads a user by username.
     *
     * @param username the username of the user to load
     * @return the UserDetails object for the specified username
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Optional<User> user = userRepo.getByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User with username " + username + " not found.");
        }
        return new UserDetails(user.get());
    }
}