package freelanceplatform.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;




@MappedSuperclass
@Data
public class AbstractEntity {
    @Id
    @GeneratedValue
    private Integer id;
}
