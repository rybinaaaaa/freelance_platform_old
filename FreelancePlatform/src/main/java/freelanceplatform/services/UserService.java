package freelanceplatform.services;

import freelanceplatform.data.UserRepository;
import freelanceplatform.exceptions.ValidationException;
import freelanceplatform.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User find(Integer id) {
        Objects.requireNonNull(id);
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void save(User user){
        Objects.requireNonNull(user);
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ValidationException("This user already exists");
        }
        user.encodePassword(passwordEncoder);
        userRepository.save(user);
    }

    @Transactional
    public void update(User user){
        Objects.requireNonNull(user);
        if (exists(user.getId())) {
            user.encodePassword(passwordEncoder);
            userRepository.save(user);
        }
    }

    @Transactional
    public void delete(User user){
        Objects.requireNonNull(user);
        if (exists(user.getId())) {
            userRepository.delete(user);
        }
    }



    public boolean exists(Integer id){
        Objects.requireNonNull(id);
        return userRepository.existsById(id);
    }

}
