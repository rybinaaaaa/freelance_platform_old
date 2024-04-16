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
public class Feedback extends AbstractEntity{

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User from;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User to;

    @Column(nullable = false)
    private Integer rating;

    @Column
    private String comment;

}
