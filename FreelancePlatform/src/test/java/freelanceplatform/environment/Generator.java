package freelanceplatform.environment;

import freelanceplatform.model.*;

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
        task.setStatus(TaskStatus.UNASSIGNED);
        task.setTitle("title" + randomInt());
        task.setProblem("problem" + randomInt());
        task.setDeadline(LocalDateTime.now().plusMonths(1));
        task.setPayment(randomDouble());
        task.setType(TaskType.DigitalMarketing);
        User customer = Generator.generateUser();
        customer.setRole(Role.USER);
        customer.addTaskToPosted(task);
        task.setCustomer(customer);
        return task;
    }

    public static Feedback generateFeedback(){
        final Feedback fb = new Feedback();
        fb.setComment("connent" + randomInt());
        fb.setRating(randomInt());

        User sender = Generator.generateUser();
        sender.setRole(Role.USER);
        fb.setSender(sender);

        User receiver = Generator.generateUser();
        receiver.setRole(Role.USER);
        fb.setReceiver(receiver);

        return fb;
    }

    public static Resume generateResume() {
        final Resume resume = new Resume();
        byte[] content = new byte[1024];
        for (int i = 0; i < content.length; i++) {
            content[i] = (byte) randomInt();
        }
        resume.setFilename("resume");
        resume.setContent(content);
        return resume;
    }

    public static Solution generateSolution(){
        final Solution solution = new Solution();
        solution.setTask(generateTask());
        solution.setDescription("description" + randomInt());
        solution.setLink("github/io");
        return solution;
    }

    public static Proposal generateProposal() {
        User user = generateUser();
        user.setRole(Role.USER);

        Task task = generateTask();
        task.setCustomer(user);
        task.setFreelancer(user);

        Proposal proposal = new Proposal();
        proposal.setTask(task);

        proposal.setFreelancer(user);

        return proposal;
    }
}