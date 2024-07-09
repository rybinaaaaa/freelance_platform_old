package freelanceplatform.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Solution extends AbstractEntity {

    @OneToOne(mappedBy = "solution", cascade = CascadeType.ALL)
    private Task task;

    @Column
    private String description;

    @Column(nullable = false)
    private String link;
}
