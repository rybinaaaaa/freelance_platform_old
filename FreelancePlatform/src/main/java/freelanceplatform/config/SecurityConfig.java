package freelanceplatform.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import freelanceplatform.security.AuthenticationFailure;
import freelanceplatform.security.AuthenticationSuccess;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Security configuration class for setting up the security settings of the application.
 * This configuration enables web security, method security, and sets up custom authentication
 * and authorization handling.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
//@Profile("!test")
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    /**
     * Constructs the SecurityConfig with the necessary dependencies
     * @param objectMapper the ObjectMapper used for JSON conversion
     */
    public SecurityConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Provides a PasswordEncoder bean that uses BCrypt hashing algorithm
     * @return the PasswordEncoder bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain.
     * Allows all requests but uses method security annotations for securing endpoints.
     * Sets up a custom authentication entry point to return 401 status.
     * Disables CSRF protection.
     * Enables CORS with permissive settings.
     * Configures custom success and failure handlers for form login.
     * @param http the HttpSecurity to modify
     * @return the configured SecurityFilterChain
     * @throws Exception in case of any configuration errors
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        final AuthenticationSuccess authSuccess = authenticationSuccess();
        // Allow through everything, it will be dealt with using security annotations on methods
        http.authorizeHttpRequests((auth) -> auth.anyRequest().permitAll())
                .httpBasic(Customizer.withDefaults())
                // Return 401 by default when attempting to access a secured endpoint
                .exceptionHandling(ehc -> ehc.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

                .csrf(AbstractHttpConfigurer::disable)

                .headers(customizer -> customizer.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                // Use custom success and failure handlers
                .formLogin(fl -> fl.successHandler(authSuccess)
                        .failureHandler(authenticationFailureHandler()))

                .logout(lgt -> lgt.logoutSuccessHandler(authSuccess))
        ;
        return http.build();
    }

    /**
     * Creates an instance of AuthenticationFailure handler.
     * @return the AuthenticationFailure handler
     */
    private AuthenticationFailure authenticationFailureHandler() {
        return new AuthenticationFailure(objectMapper);
    }

    /**
     * Creates an instance of AuthenticationSuccess handler.
     * @return the AuthenticationSuccess handler
     */
    private AuthenticationSuccess authenticationSuccess() {
        return new AuthenticationSuccess(objectMapper);
    }
}