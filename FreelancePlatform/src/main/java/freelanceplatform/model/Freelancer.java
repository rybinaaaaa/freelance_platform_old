package freelanceplatform.model;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class Freelancer extends User{

    @OneToMany(mappedBy = "freelancer", cascade = CascadeType.ALL)
    private List<Proposal> proposals;

    @OneToMany(mappedBy = "freelancer", cascade = CascadeType.ALL)
    private List<Task> takenTasks;

}
