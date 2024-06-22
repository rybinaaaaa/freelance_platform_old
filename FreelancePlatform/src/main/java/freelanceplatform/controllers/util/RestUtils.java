package freelanceplatform.controllers.util;

import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public class RestUtils {

    /**
     * Creates a HttpHeaders object with the provided path and URI variable values, setting the LOCATION header based on the current request URI.
     *
     * @param path              the path to append to the current request URI
     * @param uriVariableValues optional URI variable values to expand the path
     * @return HttpHeaders object containing the LOCATION header with the updated URI
     */
    public static HttpHeaders createLocationHeaderFromCurrentUri(String path, Object... uriVariableValues) {
        assert path != null;

        final URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path(path).buildAndExpand(
                uriVariableValues).toUri();
        final HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.LOCATION, location.toASCIIString());
        return headers;
    }
}
