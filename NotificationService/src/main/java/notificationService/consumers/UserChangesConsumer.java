package notificationService.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import notificationService.notificationStrategies.SendAllUsersStrategy;
import notificationService.notificationStrategies.SendCustomerStrategy;
import notificationService.notificationStrategies.SendEmailStrategy;
import notificationService.notificationStrategies.SendFreelancerStrategy;
import notificationService.service.EmailSenderService;
import notificationService.service.NotificationSender;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;



@Slf4j
@Component
public class UserChangesConsumer extends ChangesConsumer {

    public UserChangesConsumer(ObjectMapper mapper, NotificationSender notificationSender, EmailSenderService emailSenderService, WebClient webClient) {
        super(mapper, notificationSender, emailSenderService, webClient);
    }

    @KafkaListener(
            topics = {"user_created","user_updated","user_deleted"},
            groupId = "myGroup"
    )
    public void consumeChange(ConsumerRecord<String, String> record) throws JsonProcessingException {
        String userJson = record.value();
        log.info("Received message: {}", userJson);
        String topic = record.topic();

        String username = mapper.readTree(userJson).get("username").asText();


        SendEmailStrategy sendEmailStrategy;
        switch (topic) {
            case "user_created" -> {
                sendEmailStrategy = new SendCustomerStrategy(webClient, emailSenderService, mapper);
                notificationSender.setStrategy(sendEmailStrategy);
                notificationSender.sendEmail(
                        null,
                        userJson,
                        "Your account has been created!",
                        String.format("Congratulations! You have successfully created your account : '%s'", username));
            }
            case "user_updated" -> {
                sendEmailStrategy = new SendCustomerStrategy(webClient, emailSenderService, mapper);
                notificationSender.setStrategy(sendEmailStrategy);
                notificationSender.sendEmail(
                        null,
                        userJson,
                        "Your profile has been updated!",
                        String.format("Your profile '%s' has been successfully updated' ", username));
            }
            case "user_deleted" -> {
                sendEmailStrategy = new SendCustomerStrategy(webClient, emailSenderService, mapper);
                notificationSender.setStrategy(sendEmailStrategy);
                notificationSender.sendEmail(
                        null,
                        userJson,
                        "Your account has been deleted",
                        String.format("Your account '%s' has been successfully deleted' ", username));
            }

        }
    }

}
