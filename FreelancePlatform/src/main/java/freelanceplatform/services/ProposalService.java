package freelanceplatform.services;

import freelanceplatform.data.ProposalRepository;
import freelanceplatform.model.Proposal;
import freelanceplatform.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProposalService {

    private final ProposalRepository proposalRepository;

    public Proposal saveProposal(Proposal proposal) {
        return proposalRepository.save(proposal);
    }

    public Optional<Proposal> getProposalById(Long id) {
        return proposalRepository.findById(id);
    }

    public List<Proposal> getAllProposals() {
        return proposalRepository.findAll();
    }

    public List<Proposal> getProposalsByFreelancer(User freelancer) {
        return proposalRepository.findByFreelancer(freelancer);
    }

    public void deleteProposalById(Long id) {
        proposalRepository.deleteById(id);
    }
}
