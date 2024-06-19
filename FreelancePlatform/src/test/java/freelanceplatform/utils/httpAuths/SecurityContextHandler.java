package freelanceplatform.utils.httpAuths;

import freelanceplatform.model.Role;
import freelanceplatform.model.User;
import freelanceplatform.services.UserService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


//TODO
@Aspect
@Component
public class SecurityContextHandler {

    @Autowired
    private UserService userService;

    @Pointcut("within(freelanceplatform.controllers.*Test) && @annotation(freelanceplatform.utils.httpAuths.WithAuthenticatedAdmin)")
    public void isUserMockedAsAdmin() {
    }

    @Pointcut("within(freelanceplatform.controllers.*Test) && @annotation(freelanceplatform.utils.httpAuths.WithAuthenticatedUser)")
    public void isUserMockedAsUser() {
    }

    @Before("isUserMockedAsAdmin()")
    public void setAdminContext() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User user = userService.find(2);

        TestingAuthenticationToken token = new TestingAuthenticationToken(user, user.getPassword(), user.getRole().name());
        context.setAuthentication(token);
        SecurityContextHolder.setContext(context);
    }

    @Before("isUserMockedAsUser()")
    public void setUserContext() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User user = userService.find(1);

        TestingAuthenticationToken token = new TestingAuthenticationToken(user, user.getPassword(), user.getRole().name());
        context.setAuthentication(token);
        SecurityContextHolder.setContext(context);
    }
}
