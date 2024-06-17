package freelanceplatform.config;
import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.unit.DataSize;

/**
 * Configuration class for file upload settings.
 * This class sets up the multipart configuration for handling file uploads.
 */
@org.springframework.context.annotation.Configuration
@ComponentScan
@EnableAutoConfiguration
public class FileUploadConfig {

    /**
     * Configures the multipart settings for file uploads.
     * Sets the maximum file size and request size to 128KB
     * @return the configured MultipartConfigElement
     */
    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.parse("128KB"));
        factory.setMaxRequestSize(DataSize.parse("128KB"));
        return factory.createMultipartConfig();
    }
}

