package freelanceplatform.environment;

import freelanceplatform.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

public class Generator {
    private static final Random RAND = new Random();

    public static int randomInt() {
        return RAND.nextInt();
    }

    public static double randomDouble() {
        return RAND.nextDouble();
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

    public static Task generateTask(){
        final Task task = new Task();
//        task.setId(randomInt());
        task.setStatus(TaskStatus.UNASSIGNED);
        task.setTitle("title" + randomInt());
        task.setProblem("problem" + randomInt());
        task.setDeadline(LocalDateTime.now().plusMonths(1));
        task.setPayment(randomDouble());
        task.setType(TaskType.DigitalMarketing);
        return task;
    }
}