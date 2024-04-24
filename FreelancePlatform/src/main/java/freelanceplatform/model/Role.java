package freelanceplatform.model;



public enum Role {
//    ADMIN("ROLE_ADMIN"), FREELANCER("ROLE_FREELANCER"), CUSTOMER("ROLE_CUSTOMER"), GUEST("ROLE_GUEST");

    ADMIN("ROLE_ADMIN"), USER("ROLE_USER"), GUEST("ROLE_GUEST");

    private final String name;

    Role(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
