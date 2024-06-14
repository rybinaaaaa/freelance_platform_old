package freelanceplatform.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"receivedFeedbacks", "sentFeedbacks", "proposals", "takenTasks"})
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, name = "first_name")
    private String firstName;

    @Column(nullable = false, name = "last_name")
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private int rating;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Task> postedTasks;

    @OneToMany(mappedBy = "freelancer", cascade = CascadeType.ALL)
    private List<Proposal> proposals;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    private List<Feedback> receivedFeedbacks;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<Feedback> sentFeedbacks;

    @OneToMany(mappedBy = "freelancer", cascade = CascadeType.ALL)
    private List<Task> takenTasks;

    public void addProposal(Proposal... proposals) {
        this.proposals.addAll(Arrays.asList(proposals));
    }

    public void addReceivedFeedback(Feedback... feedbacks) {
        this.receivedFeedbacks.addAll(Arrays.asList(feedbacks));
    }

    public void addSentFeedback(Feedback... feedbacks) {
        this.sentFeedbacks.addAll(Arrays.asList(feedbacks));
    }

    public void deleteProposal(Proposal proposal) {
        this.proposals.remove(proposal);
    }


    public void deleteReceivedFeedback(Feedback feedback) {
        this.receivedFeedbacks.remove(feedback);
    }

    public void deleteSentFeedback(Feedback feedback) {
        this.sentFeedbacks.remove(feedback);
    }

    public void encodePassword(PasswordEncoder encoder) {
        this.password = encoder.encode(password);
    }

    public void addTaskToPosted(Task task) {
        if (postedTasks == null) postedTasks = new ArrayList<>();
        this.postedTasks.add(task);
    }

    public void addTaskToTaken(Task task) {
        if (takenTasks == null) takenTasks = new ArrayList<>();
        this.takenTasks.add(task);
    }

    public void removePostedTask(Task task) {
        if (this.postedTasks != null) this.postedTasks.remove(task);
    }

    public void removeTakenTask(Task task) {
        if (this.takenTasks != null) this.takenTasks.remove(task);
    }

    @JsonIgnore
    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
}
