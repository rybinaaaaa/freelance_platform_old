package freelanceplatform.kafka.topics;


public enum TaskChangesTopic {

    TaskPosted("Task was posted"),
    FreelancerAssigned("Freelancer was assigned"),
    TaskAccepted("Task was accepted"),
    FreelancerRemoved("Freelancer was removed"),
    TaskSendOnReview("Task was send on review");

    private final String value;

    TaskChangesTopic(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}

