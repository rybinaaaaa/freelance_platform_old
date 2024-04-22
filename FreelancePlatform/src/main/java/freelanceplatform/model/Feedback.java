package freelanceplatform.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Feedback extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "from_user_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "to_user_id", nullable = false)
    private User receiver;

    @Column(nullable = false)
    private Integer rating;

    @Column
    private String comment;
}
