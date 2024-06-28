package notificationService.notificationStrategies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import notificationService.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public abstract class SendEmailStrategy {


    protected final WebClient webClient;

    protected final EmailSenderService emailSender;

    protected final ObjectMapper mapper;

    @Autowired
    public SendEmailStrategy(WebClient webClient, EmailSenderService emailSender, ObjectMapper mapper) {
        this.webClient = webClient;
        this.emailSender = emailSender;
        this.mapper = mapper;
    }

    public abstract void sendEmail(String taskJson, String userJson , String subject, String body) throws JsonProcessingException;
}
