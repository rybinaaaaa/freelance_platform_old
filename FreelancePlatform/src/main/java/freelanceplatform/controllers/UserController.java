package freelanceplatform.controllers;

import freelanceplatform.controllers.util.RestUtils;
import freelanceplatform.dto.Mapper;
import freelanceplatform.dto.entityCreationDTO.UserCreationDTO;
import freelanceplatform.dto.entityDTO.UserDTO;
import freelanceplatform.exceptions.NotFoundException;
import freelanceplatform.exceptions.ValidationException;
import freelanceplatform.model.Resume;
import freelanceplatform.model.Task;
import freelanceplatform.model.TaskStatus;
import freelanceplatform.model.User;
import freelanceplatform.security.model.UserDetails;
import freelanceplatform.services.TaskService;
import freelanceplatform.services.UserService;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/rest/users")
@PreAuthorize("permitAll()")
//@CacheConfig(cacheNames = "students")
public class UserController {

    private final UserService userService;
    private final TaskService taskService;
    private final Mapper mapper;

    @Autowired
    public UserController(UserService userService, TaskService taskService, Mapper mapper) {
        this.userService = userService;
        this.taskService = taskService;
        this.mapper = mapper;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO getUserById(@PathVariable Integer id) {
        try {
            log.info("fetching with db");
            return mapper.userToDTO(userService.find(id));
        } catch (NotFoundException e) {
            throw NotFoundException.create("User", id);
        }
    }

    @GetMapping(value = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
//    @Cacheable(key = "#username")
    public UserDTO getUserByUsername(@PathVariable String username) {
        try {
            return mapper.userToDTO(userService.findByUsername(username));
        } catch (NotFoundException e) {
            throw NotFoundException.create("User", username);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<UserDTO> getAllUsers() {
        Iterable<User> users = userService.findAll();

        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            userDTOs.add(mapper.userToDTO(user));
        }

        return userDTOs;
    }

    @PreAuthorize("(!#userCreationDTO.isAdmin() && anonymous) || hasRole('ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> signUp(@RequestBody UserCreationDTO userCreationDTO) {
        User user = mapper.userDTOToUser(userCreationDTO);
        userService.save(user);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/current");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_GUEST')")
    @GetMapping(value = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO getCurrent(Authentication auth) {
        assert auth.getPrincipal() instanceof UserDetails;
        return mapper.userToDTO(((UserDetails) auth.getPrincipal()).getUser());
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/update")
    public ResponseEntity<Void> updateUser(@RequestBody UserDTO userDTOToUpdate, Authentication auth) {
        try {
            final User user = ((UserDetails) auth.getPrincipal()).getUser();
            final User userToUpdate = userService.findByUsername(userDTOToUpdate.getUsername());
            if (!user.getId().equals(userToUpdate.getId())) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            userToUpdate.setFirstName(userDTOToUpdate.getFirstName());
            userToUpdate.setLastName(userDTOToUpdate.getLastName());
            userToUpdate.setEmail(userDTOToUpdate.getEmail());
            userService.update(userToUpdate);
            final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/current");
            return new ResponseEntity<>(headers, HttpStatus.OK);
        } catch (NotFoundException e) {
            throw NotFoundException.create("User", userDTOToUpdate.getUsername());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserByAdmin(@PathVariable Integer id) {
        final User userToDelete = userService.find(id);
        if (userToDelete != null) {
            userService.delete(userToDelete);
        } else throw NotFoundException.create("User", id);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(Authentication auth){
        final User userToDelete = ((UserDetails) auth.getPrincipal()).getUser();
        if (userToDelete != null) {
            userService.delete(userToDelete);
        } else throw new NotFoundException("User does not exist");
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/myResume")
    public Resume getResume(Authentication auth) {
        final User user = ((UserDetails) auth.getPrincipal()).getUser();
        try {
            return userService.getUsersResume(user);
        } catch (NotFoundException e) {
            throw new NotFoundException("Resume does not exist");
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/addResume")
    public ResponseEntity<Void> saveResume(@RequestParam("filename") String filename,
                                           @RequestParam("content") MultipartFile file,
                                           Authentication auth) {
        final User user = ((UserDetails) auth.getPrincipal()).getUser();
        try {
            userService.saveResume(filename, file.getBytes(), user);
            final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/current");
            return new ResponseEntity<>(headers, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
