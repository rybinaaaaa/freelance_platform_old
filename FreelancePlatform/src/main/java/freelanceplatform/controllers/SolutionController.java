package freelanceplatform.controllers;

import freelanceplatform.exceptions.NotFoundException;
import freelanceplatform.model.Solution;
import freelanceplatform.services.SolutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/rest/solutions")
@PreAuthorize("permitAll()")
public class SolutionController {

    private final SolutionService solutionService;

    @Autowired
    public SolutionController(SolutionService solutionService) {
        this.solutionService = solutionService;
    }

    /**
     * Saves a new solution.
     *
     * @param solution Solution object to be saved.
     * @return ResponseEntity indicating success and URI of the newly created resource.
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> save(@RequestBody Solution solution){
        solutionService.save(solution);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(solution.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    /**
     * Retrieves a solution by its ID.
     *
     * @param id ID of the solution to retrieve.
     * @return ResponseEntity containing the retrieved Solution object if found, or 404 if not found.
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Solution> getById(@PathVariable Integer id){
        try {
            return ResponseEntity.ok(solutionService.getById(id));
        } catch (NotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Updates details of an existing solution.
     *
     * @param id              ID of the solution to update.
     * @param updatedSolution Updated details of the solution.
     * @return ResponseEntity indicating success or failure of the update operation.
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping(value = "/{id}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody Solution updatedSolution){
        final Solution solution = solutionService.getById(id);
        solution.setLink(updatedSolution.getLink());
        solution.setDescription(updatedSolution.getDescription());
        try {
            solutionService.update(solution);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a solution.
     *
     * @param id ID of the solution to delete.
     * @return ResponseEntity indicating success or failure of the delete operation.
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping(value = "/{id}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable Integer id){
        final Solution solution = solutionService.getById(id);
        try {
            solutionService.delete(solution);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
