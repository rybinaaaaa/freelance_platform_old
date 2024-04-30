package freelanceplatform.controllers;

import freelanceplatform.dto.Mapper;
import freelanceplatform.dto.entityDTO.FeedbackReadDTO;
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
    public ResponseEntity<FeedbackReadDTO> findById(@PathVariable Integer id) {
        return feedbackService.findById(id).map(fb -> ResponseEntity.ok(mapper.feedbackToFeedbackReadDTO(fb))).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping()
    public ResponseEntity<List<FeedbackReadDTO>> findAll() {
        return ResponseEntity.ok(feedbackService.findAll().stream().map(mapper::feedbackToFeedbackReadDTO).toList());
    }

    @PreAuthorize("hasRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody FeedbackReadDTO feedbackReadDTO) {
        Feedback newFb = mapper.feedbackReadDTOToFeedback(feedbackReadDTO);
        return feedbackService.findById(id).map(fb -> {
            feedbackService.save(new Feedback(
                    id,
                    newFb.getSender(),
                    newFb.getReceiver(),
                    newFb.getRating(),
                    newFb.getComment()
            ));
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole({'ROLE_GUEST', 'ROLE_ADMIN'})")
    @PostMapping()
    public ResponseEntity<Object> save(@RequestBody FeedbackReadDTO feedbackReadDTO) {
        Feedback newFb = mapper.feedbackReadDTOToFeedback(feedbackReadDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(feedbackService.save(newFb).getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        return feedbackService.deleteById(id)? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
