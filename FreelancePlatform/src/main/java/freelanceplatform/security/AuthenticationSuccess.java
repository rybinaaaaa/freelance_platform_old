package freelanceplatform.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import freelanceplatform.security.model.LoginStatus;
import freelanceplatform.security.model.UserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

public class AuthenticationSuccess implements AuthenticationSuccessHandler, LogoutSuccessHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationSuccess.class);

    private final ObjectMapper mapper;

    public AuthenticationSuccess(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Handles successful authentication.
     *
     * @param httpServletRequest  The request being handled.
     * @param httpServletResponse The response to redirect or write to.
     * @param authentication      The authenticated principal after successful authentication.
     * @throws IOException If an I/O error occurs during the handling of the authentication success.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException {
        final String username = getUsername(authentication);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Successfully authenticated user {}", username);
        }
        final LoginStatus loginStatus = new LoginStatus(true, authentication.isAuthenticated(), username, null);
        mapper.writeValue(httpServletResponse.getOutputStream(), loginStatus);
    }

    /**
     * Retrieves the username from the authentication principal.
     *
     * @param authentication The authentication object containing the principal.
     * @return The username of the authenticated user, or an empty string if authentication is null.
     */
    private String getUsername(Authentication authentication) {
        if (authentication == null) {
            return "";
        }
        return ((UserDetails) authentication.getPrincipal()).getUsername();
    }

    /**
     * Handles successful logout.
     *
     * @param httpServletRequest  The request being handled.
     * @param httpServletResponse The response to redirect or write to.
     * @param authentication      The authentication representing the user who logged out.
     * @throws IOException If an I/O error occurs during the handling of the logout success.
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                Authentication authentication) throws IOException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Successfully logged out user {}", getUsername(authentication));
        }
        final LoginStatus loginStatus = new LoginStatus(false, true, null, null);
        mapper.writeValue(httpServletResponse.getOutputStream(), loginStatus);
    }
}
