package freelanceplatform.environment;

import freelanceplatform.model.User;

import java.util.Random;

public class Generator {
    private static final Random RAND = new Random();

    public static int randomInt() {
        return RAND.nextInt();
    }

    public static int randomInt(int max) {
        return RAND.nextInt(max);
    }

    public static boolean randomBoolean() {
        return RAND.nextBoolean();
    }

    public static User generateUser() {
        final User user = new User();
        user.setUsername("username" + randomInt());
        user.setFirstName("firstname" + randomInt());
        user.setLastName("lastname" + randomInt());
        user.setEmail("email" + randomInt());
        user.setPassword("password " + randomInt());
        return user;
    }
}