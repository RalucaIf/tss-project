package echipa13.calatorii.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /imagine/** din browser → /imagine/ pe disc
        registry.addResourceHandler("/imagine/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/imagine/");
    }
}
