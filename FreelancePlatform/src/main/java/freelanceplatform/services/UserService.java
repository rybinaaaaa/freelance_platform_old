package freelanceplatform.services;

import freelanceplatform.data.UserRepository;
import freelanceplatform.exceptions.ValidationException;
import freelanceplatform.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class UserService {
    private final UserRepository userRepo;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User get(Integer id) {
        Objects.requireNonNull(id);
        return userRepo.findById(id).orElse(null);
    }

    @Transactional
    public User getByUsername(String username) {
        Objects.requireNonNull(username);
        return userRepo.getByUsername(username);
    }

    @Transactional
    public Iterable<User> getAll() {
        return userRepo.findAll();
    }

    @Transactional
    public void save(User user){
        Objects.requireNonNull(user);
        if (userRepo.existsByUsername(user.getUsername())) {
            throw new ValidationException("This user already exists");
        }
        user.encodePassword(passwordEncoder);
        userRepo.save(user);
    }

    @Transactional
    public void update(User user){
        Objects.requireNonNull(user);
        if (exists(user.getId())) {
            user.encodePassword(passwordEncoder);
            userRepo.save(user);
        }
    }

    @Transactional
    public void delete(User user){
        Objects.requireNonNull(user);
        if (exists(user.getId())) {
            userRepo.delete(user);
        }
    }



    public boolean exists(Integer id){
        Objects.requireNonNull(id);
        return userRepo.existsById(id);
    }

}
