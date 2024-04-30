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
    @JoinColumn(name = "from_user_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "to_user_id", nullable = false)
    private User receiver;

    @Column(nullable = false)
    private Integer rating;

    @Column
    private String comment;

    public Feedback(Integer id, User sender, User receiver, Integer rating, String comment) {
        setId(id);
        this.sender = sender;
        this.receiver = receiver;
        this.rating = rating;
        this.comment = comment;
    }
}
