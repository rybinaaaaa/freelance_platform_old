package freelanceplatform.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class Customer extends User{

    @OneToMany(mappedBy = "customer")
    private List<Task> postedTasks;
}

