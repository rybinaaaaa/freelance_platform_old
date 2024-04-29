package freelanceplatform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Resume extends AbstractEntity{

    @Column(nullable = false)
    private String filename;

    @Lob
    @Column(nullable = false)
    private byte[] content;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

}
