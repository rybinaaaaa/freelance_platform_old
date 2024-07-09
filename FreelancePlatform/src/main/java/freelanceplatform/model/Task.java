package freelanceplatform.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"solution"})
public class Task extends AbstractEntity {

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "freelancer_id")
    private User freelancer;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String problem;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType type;

    @Column(nullable = false)
    private Double payment;

    @Column
    private LocalDateTime assignedDate;

    @Column
    private LocalDateTime submittedDate;

    @Column
    private LocalDateTime postedDate;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "solution_id")
    private Solution solution;

    public Task(User customer, String title, String problem, LocalDateTime deadline, Double payment, TaskType type) {
        this.customer = customer;
        this.title = title;
        this.problem = problem;
        this.deadline = deadline;
        this.status = TaskStatus.UNASSIGNED;
        this.payment = payment;
        this.type = type;
        this.postedDate = LocalDateTime.now();
    }
}
