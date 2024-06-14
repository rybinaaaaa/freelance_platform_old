package freelanceplatform.controllers;

import freelanceplatform.dto.Mapper;
import freelanceplatform.dto.entityDTO.FeedbackDTO;
import freelanceplatform.exceptions.NotFoundException;
import freelanceplatform.model.Feedback;
import freelanceplatform.services.FeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/rest/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final Mapper mapper;

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

    @PreAuthorize("hasRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody FeedbackDTO feedbackDTO) {
        Feedback newFb = mapper.feedbackDtoToFeedback(feedbackDTO);
        try {
            feedbackService.update(new Feedback(
                    id,
                    newFb.getSender(),
                    newFb.getReceiver(),
                    newFb.getRating(),
                    newFb.getComment()
            ));
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole({'ROLE_GUEST', 'ROLE_ADMIN'})")
    @PostMapping()
    public ResponseEntity<Object> save(@RequestBody FeedbackDTO feedbackDTO) {
        Feedback newFb = mapper.feedbackDtoToFeedback(feedbackDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(feedbackService.save(newFb).getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        return feedbackService.deleteById(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
