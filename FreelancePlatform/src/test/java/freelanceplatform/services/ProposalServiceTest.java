package freelanceplatform.services;

import freelanceplatform.data.ProposalRepository;
import freelanceplatform.data.TaskRepository;
import freelanceplatform.data.UserRepository;
import freelanceplatform.model.Proposal;
import freelanceplatform.model.Task;
import freelanceplatform.model.User;
import freelanceplatform.utils.CacheableTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ProposalServiceTest extends CacheableTestBase {

    private final ProposalRepository proposalRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ProposalService proposalService;

    @Autowired
    public ProposalServiceTest(ProposalRepository proposalRepository, UserRepository userRepository, TaskRepository taskRepository, ProposalService proposalService) {
        this.proposalRepository = proposalRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.proposalService = proposalService;
    }

    @Override
    protected String getCacheName() {
        return "proposals";
    }

    @Test
    void testSaveProposal() {
        User freelancer = userRepository.findById(1).orElseThrow();
        Task task = taskRepository.findById(1).orElseThrow();

        Proposal proposal = new Proposal();
        proposal.setFreelancer(freelancer);
        proposal.setTask(task);

        Proposal savedProposal = proposalService.save(proposal);

        assertNotNull(savedProposal);
        assertEquals(freelancer.getId(), savedProposal.getFreelancer().getId());
        assertEquals(task.getId(), savedProposal.getTask().getId());

        Optional<Proposal> foundProposal = proposalRepository.findById(savedProposal.getId());
        assertTrue(foundProposal.isPresent());
        assertEquals(savedProposal.getId(), foundProposal.get().getId());

        assertTrue(Optional.ofNullable(cacheManager.getCache(cacheName).get(savedProposal.getId())).isPresent());
    }

    @Test
    void testFindById() {
        int id = 1;

        Optional<Proposal> foundProposal = proposalService.findById(id);

        assertTrue(foundProposal.isPresent());
        assertEquals(id, foundProposal.get().getId());

        assertTrue(Optional.ofNullable(cacheManager.getCache(cacheName).get(id)).isPresent());
    }

    @Test
    void testFindByIdNotFound() {
        assertFalse(proposalService.findById(999).isPresent());
    }

    @Test
    void testDeleteById() {
        int id = 1;

        assertTrue(proposalService.deleteById(id));
        Optional<Proposal> foundProposal = proposalRepository.findById(id);
        assertFalse(foundProposal.isPresent());

        assertFalse(Optional.ofNullable(cacheManager.getCache(cacheName).get(id)).isPresent());
    }

    @Test
    void testDeleteByIdNotFound() {
        assertFalse(proposalService.deleteById(999));
    }

    @Test
    void testFindByFreelancer() {
        User freelancer = userRepository.findById(1).orElseThrow();

        List<Proposal> proposals = proposalService.findByFreelancer(freelancer);

        assertNotNull(proposals);
        assertFalse(proposals.isEmpty());
        proposals.forEach(proposal -> assertEquals(freelancer.getId(), proposal.getFreelancer().getId()));
    }
}
