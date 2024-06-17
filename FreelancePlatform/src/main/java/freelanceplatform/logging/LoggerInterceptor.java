package freelanceplatform.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Enumeration;

/**
 * Interceptor class for logging HTTP requests.
 * This class implements the {@link HandlerInterceptor} interface to log details of incoming HTTP requests.
 */
@Component
public class LoggerInterceptor implements HandlerInterceptor {
    private static Logger log = LoggerFactory.getLogger(LoggerInterceptor.class);

    /**
     * Intercepts the execution of a handler before the actual handler is executed.
     * Logs the HTTP request details.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @param handler  the handler to execute
     * @return {@code true} to proceed with the next interceptor or the handler itself, otherwise {@code false}
     * @throws Exception in case of errors
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("[preHandle][" + request + "]" + "[" + request.getMethod() + "]"
                + "[" + request.getRequestURI() + "]" + "[" + getParameters(request) + "]");
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    /**
     * Retrieves and formats the request parameters from the HTTP request.
     *
     * @param request the HTTP request
     * @return a formatted string of request parameters
     */
    private String getParameters(HttpServletRequest request) {
        StringBuffer posted = new StringBuffer();
        Enumeration<?> e = request.getParameterNames();
        if (e != null) {
            posted.append("?");
        }
        while (e.hasMoreElements()) {
            if (posted.length() > 1) {
                posted.append("&");
            }
            String curr = (String) e.nextElement();
            posted.append(curr + "=");
            if (curr.contains("password")
                    || curr.contains("pass")
                    || curr.contains("pwd")) {
                posted.append("*****");
            } else {
                posted.append(request.getParameter(curr));
            }
        }
        String ip = request.getHeader("X-FORWARDED-FOR");
        String ipAddr = (ip == null) ? getRemoteAddr(request) : ip;
        if (ipAddr!=null && !ipAddr.equals("")) {
            posted.append("&_psip=" + ipAddr);
        }
        return posted.toString();
    }

    /**
     * Retrieves the remote address from the HTTP request.
     *
     * @param request the HTTP request
     * @return the remote address as a string
     */
    private String getRemoteAddr(HttpServletRequest request) {
        String ipFromHeader = request.getHeader("X-FORWARDED-FOR");
        if (ipFromHeader != null && ipFromHeader.length() > 0) {
            log.debug("ip from proxy - X-FORWARDED-FOR : " + ipFromHeader);
            return ipFromHeader;
        }
        return request.getRemoteAddr();
    }
}
