package freelanceplatform.utils.httpAuths;

import freelanceplatform.model.Role;
import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@WithMockUser(username = "test", password = "test", authorities = {"USER"})
public @interface WithAuthentificatedUser {
}