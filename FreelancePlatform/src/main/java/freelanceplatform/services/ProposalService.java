package freelanceplatform.services;

import freelanceplatform.data.ProposalRepository;
import freelanceplatform.data.TaskRepository;
import freelanceplatform.data.UserRepository;
import freelanceplatform.exceptions.NotFoundException;
import freelanceplatform.model.Proposal;
import freelanceplatform.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public Proposal update(Proposal proposal) {
        return proposalRepository.findById(proposal.getId()).map(pr -> {
            Optional.ofNullable(pr.getFreelancer()).ifPresent(fr -> fr.deleteProposal(pr));
            Optional.ofNullable(proposal.getFreelancer()).ifPresent(fr -> fr.addProposal(pr));
            return proposalRepository.save(proposal);
        }).orElseThrow(() -> new NotFoundException("User with id " + proposal.getId() + " not found"));
    }

    @Transactional
    public Proposal save(Proposal proposal) {
        Optional.ofNullable(proposal.getFreelancer()).flatMap(maybeFreelancer -> userRepository.findById(maybeFreelancer.getId()))
                .ifPresent(freelancer1 -> freelancer1.addProposal(proposal));
        Optional.ofNullable(proposal.getTask()).ifPresent(
                maybeTask -> {
                    Optional.ofNullable(maybeTask.getId())
                            .flatMap(taskRepository::findById)
                            .orElseThrow(() -> new NotFoundException("User with id " + proposal.getId() + " not found"));
                }
        );
        return proposalRepository.save(proposal);
    }

    @Transactional(readOnly = true)
    public Optional<Proposal> findById(Integer id) {
        return proposalRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Proposal> findAll() {
        return proposalRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Proposal> findByFreelancer(User freelancer) {
        return proposalRepository.findByFreelancer(freelancer);
    }

    @Transactional
    public boolean deleteById(Integer id) {
        return proposalRepository.findById(id)
                .map(proposal -> {
                    proposalRepository.delete(proposal);
                    return true;
                }).orElse(false);
    }
}
