package freelanceplatform.kafka.topics;

public enum UserChangesTopic {

    UserCreated("user_created"),
    UserUpdated("user_updated"),
    UserDeleted("user_deleted");

    private final String value;

    UserChangesTopic(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
