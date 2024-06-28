package notificationService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Setter;
import notificationService.notificationStrategies.SendEmailStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class NotificationSender {

    @Setter
    private SendEmailStrategy strategy;

    public void sendEmail(String taskJson, String userJson , String subject, String body) throws JsonProcessingException {
        strategy.sendEmail(taskJson, userJson , subject, body);
    }

}
