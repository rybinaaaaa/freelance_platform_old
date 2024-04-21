package freelanceplatform.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
public class Task extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
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

    @Column(nullable = false)
    private Double payment;

    @Column
    private LocalDateTime assignedDate;

    @Column
    private LocalDateTime submittedDate;

    @Column
    private String revisions;

    @Lob
    @Column
    private byte[] solution;

    public Task(User customer, String title, String problem, LocalDateTime deadline, Double payment) {
        this.customer = customer;
        this.title = title;
        this.problem = problem;
        this.deadline = deadline;
        this.status = TaskStatus.UNASSIGNED;
        this.payment = payment;
    }


    @Override
    public String toString() {
        return "Task{" +
                "customer=" + customer.getUsername() +
                ", freelancer=" + freelancer.getUsername() +
                ", title='" + title + '\'' +
                ", problem='" + problem + '\'' +
                ", deadline=" + deadline +
                ", status=" + status +
                ", payment=" + payment +
                ", assignedDate=" + assignedDate +
                ", submittedDate=" + submittedDate +
                ", revisions='" + revisions + '\'' +
                '}';
    }
}
