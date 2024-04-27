package freelanceplatform.data;

import freelanceplatform.model.Task;
import freelanceplatform.model.TaskStatus;
import freelanceplatform.model.TaskType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends CrudRepository<Task, Integer> {

    //TASK BOARD
    @Query(value = "select t from Task t where t.status = :status order by t.postedDate desc")
    Iterable<Task> findAllByStatusFromNewest(TaskStatus status);

    @Query(value = "select t from Task t where t.status = :status order by t.postedDate")
    Iterable<Task> findAllByStatusFromOldest(TaskStatus status);

    @Query(value = "select t from Task t where t.type = :type and t.status = :status order by t.postedDate desc")
    Iterable<Task> findAllByTypeAndStatusFromNewest(TaskType type, TaskStatus status);

    @Query(value = "select t from Task t where t.type = :type and t.status = :status order by t.postedDate")
    Iterable<Task> findAllByTypeAndStatusFromOldest(TaskType type, TaskStatus status);

    //TAKEN TASKS
    @Query(value = "select t from Task t where t.freelancer.id = :freelancerId and t.deadline > CURRENT_TIMESTAMP")
    Iterable<Task> findAllTakenByFreelancerIdDeadlineNotExpired(Integer freelancerId);

    @Query(value = "select t from Task t where t.freelancer.id = :freelancerId and t.deadline < CURRENT_TIMESTAMP")
    Iterable<Task> findAllTakenByFreelancerIdDeadlineExpired(Integer freelancerIdId);

    @Query(value = "select t from Task t where t.freelancer.id = :freelancerId and t.status = :taskStatus and t.deadline > CURRENT_TIMESTAMP")
    Iterable<Task> findAllTakenByFreelancerIdAndStatusDeadlineNotExpired(Integer freelancerId, TaskStatus taskStatus);

    @Query(value = "select t from Task t where t.freelancer.id = :freelancerId and t.status = :taskStatus and t.deadline > CURRENT_TIMESTAMP")
    Iterable<Task> findAllTakenByFreelancerIdAndStatusDeadlineExpired(Integer freelancerId, TaskStatus taskStatus);

    //POSTED TASKS
    @Query(value = "select t from Task t where t.customer.id = :customerId and t.deadline > CURRENT_TIMESTAMP")
    Iterable<Task> findAllPostedByCustomerIdDeadlineNotExpired(Integer customerId);

    @Query(value = "select t from Task t where t.customer.id = :customerId and t.deadline < CURRENT_TIMESTAMP")
    Iterable<Task> findAllPostedByCustomerIdDeadlineExpired(Integer customerId);

    @Query(value = "select t from Task t where t.customer.id = :customerId and t.status = :taskStatus and t.deadline > CURRENT_TIMESTAMP")
    Iterable<Task> findAllPostedByCustomerIdAndStatusDeadlineNotExpired(Integer customerId, TaskStatus taskStatus);

    @Query(value = "select t from Task t where t.customer.id = :customerId and t.status = :taskStatus and t.deadline > CURRENT_TIMESTAMP")
    Iterable<Task> findAllPostedByCustomerIdAndStatusDeadlineExpired(Integer customerId, TaskStatus taskStatus);
}
