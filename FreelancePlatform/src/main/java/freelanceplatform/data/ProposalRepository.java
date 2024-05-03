package freelanceplatform.data;

import freelanceplatform.model.Proposal;
import freelanceplatform.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProposalRepository extends CrudRepository<Proposal, Integer> {

    List<Proposal> findAll();

    List<Proposal> findByFreelancer(User freelancer);
}
