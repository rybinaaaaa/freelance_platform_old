package freelanceplatform.controllers;

import freelanceplatform.controllers.util.RestUtils;
import freelanceplatform.exceptions.NotFoundException;
import freelanceplatform.model.Resume;
import freelanceplatform.model.User;
import freelanceplatform.security.model.UserDetails;
import freelanceplatform.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/rest/users")
@PreAuthorize("permitAll()")
@CacheConfig(cacheNames = "students")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(key = "#id")
    public User getUserById(@PathVariable Integer id) {
        try {
            log.info("fetching with db");
            return userService.find(id);
        } catch (NotFoundException e) {
            throw NotFoundException.create("User", id);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<User> getAllUsers() {
        return userService.findAll();
    }

    @PreAuthorize("(!#user.isAdmin() && anonymous) || hasRole('ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> signUp(@RequestBody User user) {
        userService.save(user);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/current");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_GUEST')")
    @GetMapping(value = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getCurrent(Authentication auth) {
        assert auth.getPrincipal() instanceof UserDetails;
        return ((UserDetails) auth.getPrincipal()).getUser();
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/update")
    public ResponseEntity<Void> updateUser(@RequestBody User userToUpdate, Authentication auth) {
        final User user = ((UserDetails) auth.getPrincipal()).getUser();
        if (!user.getId().equals(userToUpdate.getId())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!userService.exists(userToUpdate.getId())){
            throw NotFoundException.create("User", userToUpdate.getId());
        }
        userService.update(userToUpdate);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/current");
        return new ResponseEntity<>(headers, HttpStatus.OK);
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
