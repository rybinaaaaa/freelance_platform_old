package notificationService.notificationStrategies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import notificationService.service.EmailSenderService;
import org.springframework.web.reactive.function.client.WebClient;

public class SendFreelancerStrategy extends SendEmailStrategy {

    public SendFreelancerStrategy(WebClient webClient, EmailSenderService emailSender, ObjectMapper mapper) {
        super(webClient, emailSender, mapper);
    }

    @Override
    public void sendEmail(String taskJson, String userJson , String subject, String body) throws JsonProcessingException {
        if (taskJson!=null){
            String toEmail = mapper.readTree(taskJson).get("freelancer").get("email").asText();
            emailSender.sendEmail(toEmail, subject, body);
        }
        if (userJson!=null){
            String toEmail = mapper.readTree(userJson).get("email").asText();
            emailSender.sendEmail(toEmail, subject, body);
        }
    }
}


