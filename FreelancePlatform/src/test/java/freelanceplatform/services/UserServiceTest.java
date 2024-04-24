package freelanceplatform.services;

import freelanceplatform.data.UserRepository;
import freelanceplatform.environment.Generator;
import freelanceplatform.exceptions.NotFoundException;
import freelanceplatform.model.Role;
import freelanceplatform.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp(){
        user = Generator.generateUser();
        user.setRole(Role.USER);
    }

    @Test
    public void saveUserActuallySaves() {
        userService.save(user);
        assertTrue(userRepository.existsByUsername(user.getUsername()));
    }

    @Test
    public void updateUserActuallyUpdates() {
        userService.save(user);
        Optional<User> newUserOptional = userRepository.getByUsername(user.getUsername());
        User newUser = newUserOptional.get();
        newUser.setFirstName("Test");
        userService.update(newUser);

        assertEquals("Test", userRepository.getByUsername(user.getUsername()).get().getFirstName());
    }

    @Test
    public void findUserThrowsExceptionIfUserNotFound() {
        assertThrows(NotFoundException.class, () -> userService.find(10));
    }

    @Test
    public void findUserByUsernameThrowsExceptionIfUserNotFound() {
        assertThrows(NotFoundException.class, () -> userService.findByUsername("test"));
    }
}
