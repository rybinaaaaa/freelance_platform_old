package freelanceplatform.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Feedback extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "to_user_id")
    private User receiver;

    @Column
    private Integer rating;

    @Column
    private String comment;
}
