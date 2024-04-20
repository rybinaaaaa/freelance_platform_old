package freelanceplatform.services;

import freelanceplatform.data.UserRepository;
import freelanceplatform.environment.Generator;
import freelanceplatform.model.Role;
import freelanceplatform.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
        user.setRole(Role.FREELANCER);
    }

    @Test
    public void saveUserActuallySaves() {
        userService.save(user);
        assertTrue(userRepository.existsByUsername(user.getUsername()));
    }

    @Test
    public void updateUserActuallyUpdates() {
        userService.save(user);
        User newUser = userRepository.getByUsername(user.getUsername()).get();
        newUser.setFirstName("Test");
        userService.update(newUser);

        assertEquals("Test", userRepository.getByUsername(user.getUsername()).get().getFirstName());
    }
}
