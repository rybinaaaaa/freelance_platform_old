package freelanceplatform.controllers;

import freelanceplatform.controllers.util.RestUtils;
import freelanceplatform.dto.Mapper;
import freelanceplatform.dto.entityCreationDTO.UserCreationDTO;
import freelanceplatform.dto.entityDTO.UserDTO;
import freelanceplatform.exceptions.NotFoundException;
import freelanceplatform.model.Resume;
import freelanceplatform.model.User;
import freelanceplatform.security.model.UserDetails;
import freelanceplatform.services.TaskService;
import freelanceplatform.services.UserService;
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

/**
 * REST controller for managing users.
 * This controller provides endpoints for user CRUD operations and other user-related actions.
 */
@Slf4j
@RestController
@RequestMapping("/rest/users")
@PreAuthorize("permitAll()")
public class UserController {

    private final UserService userService;
    private final TaskService taskService;
    private final Mapper mapper;

    /**
     * Constructs the UserController with the necessary dependencies
     * @param userService the service for managing users
     * @param taskService the service for managing tasks
     * @param mapper the mapper for converting between entities and DTOs
     */
    @Autowired
    public UserController(UserService userService, TaskService taskService, Mapper mapper) {
        this.userService = userService;
        this.taskService = taskService;
        this.mapper = mapper;
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user
     * @return the ResponseEntity with the user data or 404 if not found
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        try {
            final UserDTO userDTO = mapper.userToDTO(userService.find(id));
            return ResponseEntity.ok(userDTO);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user
     * @return the ResponseEntity with the user data or 404 if not found
     */
    @GetMapping(value = "/username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> getUserByUserName(@PathVariable String username) {
        try {
            final UserDTO userDTO = mapper.userToDTO(userService.findByUsername(username));
            return ResponseEntity.ok(userDTO);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves all users.
     *
     * @return an iterable of all user DTOs
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<UserDTO> getAllUsers() {
        Iterable<User> users = userService.findAll();

        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            userDTOs.add(mapper.userToDTO(user));
        }

        return userDTOs;
    }

    /**
     * Signs up a new user.
     *
     * @param userCreationDTO the user creation data transfer object
     * @return the ResponseEntity indicating the result of the operation
     */
//    @PreAuthorize("(!#userCreationDTO.isAdmin() && anonymous) || hasRole('ROLE_ADMIN')")
    @PreAuthorize("!#userCreationDTO.isAdmin()")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> signUp(@RequestBody UserCreationDTO userCreationDTO) {
        User user = mapper.userDTOToUser(userCreationDTO);
        userService.save(user);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/current");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    /**
     * Retrieves the current authenticated user.
     *
     * @param auth the authentication object
     * @return the user DTO of the current user
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_GUEST')")
    @GetMapping(value = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO getCurrent(Authentication auth) {
        assert auth.getPrincipal() instanceof UserDetails;
        return mapper.userToDTO(((UserDetails) auth.getPrincipal()).getUser());
    }

    /**
     * Updates a user's information.
     *
     * @param id the ID of the user to update
     * @param userDTOToUpdate the user data to update
     * @param auth the authentication object
     * @return the ResponseEntity indicating the result of the operation
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Integer id,
                                           @RequestBody UserDTO userDTOToUpdate, Authentication auth) {
        try {
            final User user = ((UserDetails) auth.getPrincipal()).getUser();

            if (!id.equals(userDTOToUpdate.getId())) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            final User userToUpdate = userService.find(id);
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
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a user by an admin.
     *
     * @param id the ID of the user to delete
     * @return the ResponseEntity indicating the result of the operation
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUserByAdmin(@PathVariable Integer id) {
        final User userToDelete = userService.find(id);
        if (userToDelete != null) {
            userService.delete(userToDelete);
            final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/");
            return new ResponseEntity<>(headers, HttpStatus.OK);
        } else return ResponseEntity.notFound().build();
    }

    /**
     * Deletes the current authenticated user's account.
     *
     * @param auth the authentication object
     * @return the ResponseEntity indicating the result of the operation
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteAccount(Authentication auth){
        final User userToDelete = ((UserDetails) auth.getPrincipal()).getUser();
        if (userToDelete != null) {
            userService.delete(userToDelete);
            final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/");
            return new ResponseEntity<>(headers, HttpStatus.OK);
        } else return ResponseEntity.notFound().build();
    }

    /**
     * Retrieves the resume of the current authenticated user.
     *
     * @param auth the authentication object
     * @return the ResponseEntity with the resume or 404 if not found
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/myResume")
    public ResponseEntity<Resume> getResume(Authentication auth) {
        final User user = ((UserDetails) auth.getPrincipal()).getUser();
        try {
            final Resume resume = userService.getUsersResume(user);
            return ResponseEntity.ok(resume);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Saves the resume of the current authenticated user.
     *
     * @param filename the filename of the resume
     * @param file the resume file
     * @param auth the authentication object
     * @return the ResponseEntity indicating the result of the operation
     */
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
