package freelanceplatform.kafka.topics;

public enum UserChangesTopic {

    UserCreated("User was created"),
    UserUpdated("User was updated"),
    UserDeleted("User was deleted");

    private final String value;

    UserChangesTopic(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
