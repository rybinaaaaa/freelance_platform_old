package freelanceplatform.controllers;

import freelanceplatform.dto.Mapper;
import freelanceplatform.dto.entityDTO.ProposalDTO;
import freelanceplatform.exceptions.NotFoundException;
import freelanceplatform.model.Proposal;
import freelanceplatform.model.User;
import freelanceplatform.security.model.UserDetails;
import freelanceplatform.services.ProposalService;
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

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
@RestController
@RequestMapping("/rest/proposals")
@RequiredArgsConstructor
public class ProposalController {

    private final ProposalService proposalService;
    private final Mapper mapper;

    private final static ResponseEntity<Void> FORBIDDEN1 = new ResponseEntity<>(FORBIDDEN);

    @GetMapping("/{id}")
    public ResponseEntity<ProposalDTO> findById(@PathVariable Integer id) {
        return proposalService.findById(id)
                .map(pr -> ResponseEntity.ok(mapper.proposalToProposalDTO(pr)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping()
    public ResponseEntity<List<ProposalDTO>> findAll() {
        return ResponseEntity.ok(proposalService.findAll().stream()
                .map(mapper::proposalToProposalDTO).toList());
    }

    @PreAuthorize("hasRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody ProposalDTO proposalDTO, Authentication auth) {
        Objects.requireNonNull(proposalDTO);
        proposalDTO.setId(id);

        if (!hasUserAccess(proposalDTO, auth)) return FORBIDDEN1;

        Proposal newPr = mapper.proposalDTOToProposal(proposalDTO);
        try {
            proposalService.update(newPr);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @PostMapping()
    public ResponseEntity<Void> save(@RequestBody ProposalDTO proposalDTO, Authentication auth) {
        Objects.requireNonNull(proposalDTO);

        if (!hasUserAccess(proposalDTO, auth)) return FORBIDDEN1;

        Proposal newPr = mapper.proposalDTOToProposal(proposalDTO);
        try {
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{id}")
                    .buildAndExpand(proposalService.save(newPr).getId()).toUri();
            return ResponseEntity.created(location).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        return proposalService.deleteById(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    private static Boolean hasUserAccess(ProposalDTO proposalDTO, Authentication auth) {
        User user = ((UserDetails) auth.getPrincipal()).getUser();
        return user.isAdmin() || user.getId().equals(proposalDTO.getFreelancerId());
    }
}
