package freelanceplatform.services;

import freelanceplatform.data.ProposalRepository;
import freelanceplatform.data.TaskRepository;
import freelanceplatform.data.UserRepository;
import freelanceplatform.exceptions.NotFoundException;
import freelanceplatform.model.Proposal;
import freelanceplatform.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing proposals.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = "proposals")
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    /**
     * Updates an existing proposal.
     *
     * @param proposal the proposal to update
     * @return the updated proposal
     */
    @Transactional
    @CachePut(key = "#proposal.id")
    public Proposal update(Proposal proposal) {
        log.info("Updating proposal with id {}", proposal.getId());
        return proposalRepository.findById(proposal.getId()).map(pr -> {
            Optional.ofNullable(pr.getFreelancer()).ifPresent(fr -> fr.deleteProposal(pr));
            Optional.ofNullable(proposal.getFreelancer()).ifPresent(fr -> fr.addProposal(pr));
            return proposalRepository.save(proposal);
        }).orElseThrow(() -> new NotFoundException("Proposal with id " + proposal.getId() + " not found"));
    }

    /**
     * Saves a new proposal.
     *
     * @param proposal the proposal to save
     * @return the saved proposal
     */
    @Transactional
    @CachePut(key = "#proposal.id")
    public Proposal save(Proposal proposal) {
        log.info("Saving new proposal with id {}", proposal.getId());
        Optional.ofNullable(proposal.getFreelancer()).flatMap(maybeFreelancer -> userRepository.findById(maybeFreelancer.getId()))
                .ifPresent(freelancer1 -> freelancer1.addProposal(proposal));
        Optional.ofNullable(proposal.getTask()).ifPresent(
                maybeTask -> {
                    Optional.ofNullable(maybeTask.getId())
                            .flatMap(taskRepository::findById)
                            .orElseThrow(() -> new NotFoundException("Task with id " + maybeTask.getId() + " not found"));
                }
        );
        return proposalRepository.save(proposal);
    }

    /**
     * Finds a proposal by its ID.
     *
     * @param id the ID of the proposal
     * @return an Optional containing the found proposal, or empty if not found
     */
    @Transactional(readOnly = true)
    @Cacheable
    public Optional<Proposal> findById(Integer id) {
        log.info("Finding proposal by id {}", id);
        return proposalRepository.findById(id);
    }

    /**
     * Finds all proposals.
     *
     * @return a list of all proposals
     */
    @Transactional(readOnly = true)
    public List<Proposal> findAll() {
        log.info("Finding all proposals");
        return proposalRepository.findAll();
    }

    /**
     * Finds proposals by their freelancer.
     *
     * @param freelancer the freelancer of the proposals
     * @return a list of proposals by the given freelancer
     */
    @Transactional(readOnly = true)
    public List<Proposal> findByFreelancer(User freelancer) {
        log.info("Finding proposals by freelancer {}", freelancer.getUsername());
        return proposalRepository.findByFreelancer(freelancer);
    }

    /**
     * Deletes a proposal by its ID.
     *
     * @param id the ID of the proposal to delete
     * @return true if the proposal was deleted, false otherwise
     */
    @Transactional
    @CacheEvict
    public boolean deleteById(Integer id) {
        log.info("Deleting proposal with id {}", id);
        return proposalRepository.findById(id)
                .map(proposal -> {
                    proposalRepository.delete(proposal);
                    return true;
                }).orElse(false);
    }
}
