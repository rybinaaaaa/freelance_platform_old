package freelanceplatform.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
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
