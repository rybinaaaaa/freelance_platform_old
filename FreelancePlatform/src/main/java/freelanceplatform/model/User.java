package freelanceplatform.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
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
    @Column(name = "role")
    private Role role;

    @OneToOne
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @OneToMany(mappedBy = "freelancer", cascade = CascadeType.ALL)
    private List<Proposal> proposals;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    private List<Feedback> receivedFeedbacks;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<Feedback> sentFeedbacks;

    @OneToMany(mappedBy = "freelancer", cascade = CascadeType.ALL)
    private List<Task> takenTasks;

    public User(String username, String firstName, String lastName, String password, String email) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
    }

//    public void addProposal(Proposal ...proposals) {
//        this.proposals.addAll(Arrays.asList(proposals));
//    };
//
//    public void addReceivedFeedback(Feedback ...feedbacks) {
//        this.receivedFeedbacks.addAll(Arrays.asList(feedbacks));
//    };
//
//    public void addSentFeedback(Feedback ...feedbacks) {
//        this.sentFeedbacks.addAll(Arrays.asList(feedbacks));
//    };
//
//    public void deleteProposal(Proposal proposal) {
//        this.proposals.remove(proposal);
//    };
//
//    public void deleteReceivedFeedback(Feedback feedback) {
//        this.receivedFeedbacks.remove(feedback);
//    };
//
//    public void deleteSentFeedback(Feedback feedback) {
//        this.sentFeedbacks.remove(feedback);
//    };

    public void encodePassword(PasswordEncoder encoder) {
        this.password = encoder.encode(password);
    }
}
