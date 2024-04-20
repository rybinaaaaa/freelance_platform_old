package freelanceplatform.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User extends AbstractEntity{

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private int rating;

    @Enumerated(EnumType.STRING)
    @Column(name = "role",nullable = false)
    private Role role;

    @OneToOne
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @OneToMany(mappedBy = "customer")
    private List<Task> postedTasks;

    @OneToMany(mappedBy = "freelancer", cascade = CascadeType.ALL)
    private List<Proposal> proposals;

    @OneToMany(mappedBy = "freelancer", cascade = CascadeType.ALL)
    private List<Task> takenTasks;

    public User(String username, String firstName, String lastName, String email, String password, Role role) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public void encodePassword(PasswordEncoder encoder) {
        this.password = encoder.encode(password);
    }
}
