package notificationService;

import notificationService.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

//import java.util.logging.Logger;

@Service
public class KafkaConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    private final UserService userService;

    @Autowired
    public KafkaConsumer(UserService userService) {
        this.userService = userService;
    }

    @KafkaListener(
            topics = "user_created",
            groupId = "myGroup"
    )
    public void consumeUser(String eventMessage) {
        LOGGER.info(String.format("Message received - %s", eventMessage));
    }

    @KafkaListener(
            topics = "task_created",
            groupId = "myGroup"
    )
    public void consumeTask(String eventMessage) {
        List<String> emails = userService.getAllUserEmails();
        emails.forEach(LOGGER::info);
        LOGGER.info(String.format("Message received - %s", eventMessage));
    }
}
