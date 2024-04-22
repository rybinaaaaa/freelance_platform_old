package freelanceplatform.model;

import org.apache.kafka.common.protocol.types.Field;

public enum Role {
    ADMIN("ROLE_ADMIN"), FREELANCER("ROLE_FREELANCER"), CUSTOMER("ROLE_CUSTOMER"), GUEST("ROLE_GUEST");

    private final String name;

    Role(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
