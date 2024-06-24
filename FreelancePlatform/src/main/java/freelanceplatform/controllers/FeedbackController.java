package freelanceplatform.controllers;

import freelanceplatform.dto.Mapper;
import freelanceplatform.dto.entityCreationDTO.FeedbackCreationDTO;
import freelanceplatform.dto.entityDTO.FeedbackDTO;
import freelanceplatform.exceptions.NotFoundException;
import freelanceplatform.model.Feedback;
import freelanceplatform.model.User;
import freelanceplatform.security.model.UserDetails;
import freelanceplatform.services.FeedbackService;
import freelanceplatform.services.security.UserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.FORBIDDEN;

/**
 * Controller for managing feedbacks.
 */
@Slf4j
@RestController
@RequestMapping("/rest/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final UserDetailsService userDetailsService;
    private final Mapper mapper;

    private final static ResponseEntity<Void> FORBIDDEN1 = new ResponseEntity<>(FORBIDDEN);
    private final static ResponseEntity<Void> BAD_REQUEST = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    /**
     * Finds a feedback by its ID.
     *
     * @param id the ID of the feedback
     * @return the feedback DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackDTO> findById(@PathVariable Integer id) {
        return feedbackService.findById(id)
                .map(fb -> ResponseEntity
                        .ok(mapper.feedbackToFeedbackDto(fb)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Finds all feedbacks.
     *
     * @return a list of all feedback DTOs
     */
    @GetMapping()
    public ResponseEntity<List<FeedbackDTO>> findAll() {
        return ResponseEntity.ok(feedbackService.findAll().stream()
                .map(mapper::feedbackToFeedbackDto).toList());
    }

    /**
     * Updates an existing feedback.
     *
     * @param id the ID of the feedback to update
     * @param feedbackDTO the feedback DTO with updated information
     * @param auth the authentication object
     * @return a response entity indicating the outcome
     */
    @PreAuthorize("hasAnyRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody FeedbackDTO feedbackDTO, Authentication auth) {
        Objects.requireNonNull(feedbackDTO);

        if (!feedbackDTO.getId().equals(id)) return BAD_REQUEST;
        if (!hasUserAccess(feedbackDTO, auth)) return FORBIDDEN1;

        Feedback newFb = mapper.feedbackDtoToFeedback(feedbackDTO);
        try {
            feedbackService.update(newFb);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Saves a new feedback.
     *
     * @param feedbackCreationDTO the feedback DTO to save
     * @param auth the authentication object
     * @return a response entity indicating the outcome
     */
    @PreAuthorize("hasAnyRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @PostMapping()
    public ResponseEntity<Void> save(@RequestBody FeedbackCreationDTO feedbackCreationDTO, Authentication auth) {
        Objects.requireNonNull(feedbackCreationDTO);
        if (!hasUserAccess(feedbackCreationDTO, auth)) return FORBIDDEN1;

        Feedback newFb = mapper.feedbackCreationDtoToFeedback(feedbackCreationDTO);

        try {
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{id}")
                    .buildAndExpand(feedbackService.save(newFb)
                            .getId()).toUri();
            return ResponseEntity.created(location).build();
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deletes a feedback by its ID.
     *
     * @param id the ID of the feedback to delete
     * @return a response entity indicating the outcome
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        return feedbackService.deleteById(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    /**
     * Checks if the authenticated user has access to the feedback.
     *
     * @param feedbackDTO the feedback DTO
     * @param auth the authentication object
     * @return true if the user has access, false otherwise
     */
    private static Boolean hasUserAccess(FeedbackDTO feedbackDTO, Authentication auth) {
        User user = ((UserDetails) auth.getPrincipal()).getUser();
        return user.isAdmin() || user.getId().equals(feedbackDTO.getSenderId());
    }

    /**
     * Checks if the authenticated user has access to the feedback.
     *
     * @param feedbackCreationDTO the feedbackCreation DTO
     * @param auth the authentication object
     * @return true if the user has access, false otherwise
     */
    private static Boolean hasUserAccess(FeedbackCreationDTO feedbackCreationDTO, Authentication auth) {
        User user = ((UserDetails) auth.getPrincipal()).getUser();
        return user.isAdmin() || user.getId().equals(feedbackCreationDTO.getSenderId());
    }
}
