package freelanceplatform.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
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

    public void addTaskToPosted(Task task){
        if (postedTasks==null) postedTasks = new ArrayList<>();
        this.postedTasks.add(task);
    }

    public void addTaskToTaken(Task task){
        if (takenTasks==null) takenTasks = new ArrayList<>();
        this.takenTasks.add(task);
    }

    public void removePostedTask(Task task){if (this.postedTasks!=null) this.postedTasks.remove(task);}

    public void removeTakenTask(Task task){if (this.takenTasks!=null) this.takenTasks.remove(task);}

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", rating=" + rating +
                ", role=" + role +
                ", resume=" + resume +
                ", postedTasks=" + (postedTasks != null ? postedTasks.stream().map(Task::getTitle).collect(Collectors.joining(", ")) : null) +
                ", proposals=" + proposals +
                ", takenTasks=" + (takenTasks != null ? takenTasks.stream().map(Task::getTitle).collect(Collectors.joining(", ")) : null) +
                '}';
    }
}
