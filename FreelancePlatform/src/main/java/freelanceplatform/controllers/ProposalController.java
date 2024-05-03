package freelanceplatform.controllers;

import freelanceplatform.dto.Mapper;
import freelanceplatform.dto.entityDTO.ProposalDTO;
import freelanceplatform.exceptions.NotFoundException;
import freelanceplatform.model.Proposal;
import freelanceplatform.services.ProposalService;
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
@RequestMapping("/rest/proposals")
@RequiredArgsConstructor
public class ProposalController {

    private final ProposalService proposalService;
    private final Mapper mapper;

    @GetMapping("/{id}")
    public ResponseEntity<ProposalDTO> findById(@PathVariable Integer id) {
        return proposalService.findById(id).map(pr -> ResponseEntity.ok(mapper.proposalToProposalDTO(pr))).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping()
    public ResponseEntity<List<ProposalDTO>> findAll() {
        return ResponseEntity.ok(proposalService.findAll().stream().map(mapper::proposalToProposalDTO).toList());
    }

    @PreAuthorize("hasRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody ProposalDTO proposalDTO) {
        Proposal newPr = mapper.proposalDTOToProposal(proposalDTO);
        try {
            proposalService.update(new Proposal(
                    id,
                    newPr.getFreelancer(),
                    newPr.getTask()
            ));
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return  ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole({'ROLE_GUEST', 'ROLE_ADMIN'})")
    @PostMapping()
    public ResponseEntity<Object> save(@RequestBody ProposalDTO proposalDTO) {
        Proposal newPr = mapper.proposalDTOToProposal(proposalDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(proposalService.save(newPr).getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        return proposalService.deleteById(id)? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
