package freelanceplatform.controllers;

import freelanceplatform.dto.Mapper;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
@RestController
@RequestMapping("/rest/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final UserDetailsService userDetailsService;
    private final Mapper mapper;

    private final static ResponseEntity<Void> FORBIDDEN1 = new ResponseEntity<>(FORBIDDEN);

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackDTO> findById(@PathVariable Integer id) {
        return feedbackService.findById(id)
                .map(fb -> ResponseEntity
                        .ok(mapper.feedbackToFeedbackDto(fb)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping()
    public ResponseEntity<List<FeedbackDTO>> findAll() {
        return ResponseEntity.ok(feedbackService.findAll().stream()
                .map(mapper::feedbackToFeedbackDto).toList());
    }

    @PreAuthorize("hasAnyRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody FeedbackDTO feedbackDTO, Authentication auth) {
        Objects.requireNonNull(feedbackDTO);
        feedbackDTO.setId(id);

        if (!hasUserAccess(feedbackDTO, auth)) return FORBIDDEN1;

        Feedback newFb = mapper.feedbackDtoToFeedback(feedbackDTO);
        try {
            feedbackService.update(newFb);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PreAuthorize("hasAnyRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @PostMapping()
    public ResponseEntity<Void> save(@RequestBody FeedbackDTO feedbackDTO, Authentication auth) {
        Objects.requireNonNull(feedbackDTO);
        if (!hasUserAccess(feedbackDTO, auth)) return FORBIDDEN1;

        Feedback newFb = mapper.feedbackDtoToFeedback(feedbackDTO);

        try {
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(feedbackService.save(newFb)
                        .getId()).toUri();
        return ResponseEntity.created(location).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        return feedbackService.deleteById(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    private static Boolean hasUserAccess(FeedbackDTO feedbackDTO, Authentication auth) {
        User user = ((UserDetails) auth.getPrincipal()).getUser();
        return user.isAdmin() || user.getId().equals(feedbackDTO.getSenderId());
    }
}
