package freelanceplatform.services;

import freelanceplatform.data.ResumeRepository;
import freelanceplatform.data.UserRepository;
import freelanceplatform.exceptions.NotFoundException;
import freelanceplatform.exceptions.ValidationException;
import freelanceplatform.model.Resume;
import freelanceplatform.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, ResumeRepository resumeRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.resumeRepository = resumeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User find(Integer id) {
        Objects.requireNonNull(id);
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) throw new NotFoundException("User with id " + id + " not found");
        return userOptional.get();
    }

    @Transactional
    public User findByUsername(String username) {
        Objects.requireNonNull(username);
        Optional<User> userOptional = userRepository.getByUsername(username);
        if (userOptional.isEmpty()) throw new NotFoundException("User with username " + username + " not found");
        return userOptional.get();
    }

    @Transactional
    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void save(User user){
        Objects.requireNonNull(user);
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ValidationException("User with this username is already exist");
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
        } else {
            throw new NotFoundException("User with id " + user.getId() + " not found");
        }
    }

    @Transactional
    public void delete(User user){
        Objects.requireNonNull(user);
        if (exists(user.getId())) {
            userRepository.delete(user);
        } else {
            throw new NotFoundException("User with id " + user.getId() + " not found");
        }
    }

    public boolean exists(Integer id){
        Objects.requireNonNull(id);
        return userRepository.existsById(id);
    }

    @Transactional
    public void saveResume(String filename, byte[] content, User user) {
        Resume resume = new Resume();
        resume.setFilename(filename);
        resume.setContent(content);
        resume.setUser(user);
        resumeRepository.save(resume);
    }

}
