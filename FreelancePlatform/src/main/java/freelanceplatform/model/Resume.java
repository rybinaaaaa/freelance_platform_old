package freelanceplatform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class Resume extends AbstractEntity{

    @Column(nullable = false)
    private String filename;

    @Column
//    or @JdbcType(VarbinaryJdbcType.class)
    @JdbcTypeCode(Types.BINARY)
    private byte[] content;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}
