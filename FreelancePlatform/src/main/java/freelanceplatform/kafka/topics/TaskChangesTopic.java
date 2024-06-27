package freelanceplatform.kafka.topics;


public enum TaskChangesTopic {

    TaskPosted("task_posted"),
    FreelancerAssigned("freelancer_assigned"),
    TaskAccepted("task_accepted"),
    FreelancerRemoved("freelancer_removed"),
    TaskSendOnReview("task_send_on_review");

    private final String value;

    TaskChangesTopic(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}

