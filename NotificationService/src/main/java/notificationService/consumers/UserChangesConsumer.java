package notificationService.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import notificationService.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class UserChangesConsumer {

    private final ObjectMapper mapper;
    private final EmailSenderService emailSenderService;

    @Autowired
    public UserChangesConsumer(ObjectMapper mapper, EmailSenderService emailSenderService) {
        this.mapper = mapper;
        this.emailSenderService = emailSenderService;
    }

    @KafkaListener(
            topics = "user_created",
            groupId = "myGroup"
    )
    public void consumeUserChange(String message) {
        try {
            Map<String, String> userMap = mapper.readValue(message, Map.class);

            String email = userMap.get("email");
            String username = userMap.get("username");
            String firstName = userMap.get("firstName");
            String lastName = userMap.get("lastName");
            String fullName = firstName + " " + lastName;

            String body = "Hi " + username + ", your account was successfully created.\nEmail - " + email + "\nFull name - " + fullName;

            emailSenderService.sendEmail(email, "account successfully created", body);
            log.info(String.format("Message received for user %s, with email - %s", username, email));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
