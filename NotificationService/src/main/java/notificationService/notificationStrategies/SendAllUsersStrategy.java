package notificationService.notificationStrategies;

import com.fasterxml.jackson.databind.ObjectMapper;
import notificationService.service.EmailSenderService;
import notificationService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;


@Component
public class SendAllUsersStrategy extends SendEmailStrategy{

    private final UserService userService;

    @Autowired
    public SendAllUsersStrategy(WebClient webClient, EmailSenderService emailSender, ObjectMapper mapper, UserService userService) {
        super(webClient, emailSender, mapper);
        this.userService = userService;
    }

    @Override
    public void sendEmail(String taskJson, String userJson , String subject, String body) {
        //todo change uri to prod host
//        List<String> toEmails = webClient
//                .get()
//                .uri("http://localhost:8080/rest/users")
//                .retrieve()
//                .bodyToFlux(UserDTO.class)
//                .map(UserDTO::getEmail)
//                .collectList()
//                .block();
        List<String> toEmails = userService.getAllUserEmails();

        if (toEmails != null) {
            toEmails.forEach(email -> emailSender.sendEmail(email, subject, body));
        }
    }
}
