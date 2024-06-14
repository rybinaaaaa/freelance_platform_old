package freelanceplatform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class Resume extends AbstractEntity{

    @Column(nullable = false)
    private String filename;

    @Lob
    @Column
    private byte[] content;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}
