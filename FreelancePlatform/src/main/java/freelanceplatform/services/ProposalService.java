package freelanceplatform.services;

import freelanceplatform.data.ProposalRepository;
import freelanceplatform.data.TaskRepository;
import freelanceplatform.data.UserRepository;
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
    public Proposal save(Proposal proposal) {
        Optional.ofNullable(proposal.getFreelancer()).ifPresent(
                maybeFreelancer -> {
                    this.getUser(maybeFreelancer.getId()).ifPresent(freelancer1 -> freelancer1.addProposal(proposal));
                }
        );
        Optional.ofNullable(proposal.getTask()).ifPresent(
                maybeTask -> {
                    Optional.ofNullable(maybeTask.getId())
                            .flatMap(taskRepository::findById)
                            .orElseThrow(RuntimeException::new);
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
    public void deleteById(Integer id) {
        proposalRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    protected Optional<User> getUser(Integer id) {
        return Optional.ofNullable(id).flatMap(userRepository::findById);
    }
}
