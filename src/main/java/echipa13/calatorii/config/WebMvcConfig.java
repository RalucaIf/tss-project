package echipa13.calatorii.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadsUri = Paths.get("uploads").toAbsolutePath().toUri().toString();
        String imagineUri = Paths.get("imagine").toAbsolutePath().toUri().toString();

        // Pozele din Jurnal (filesystem) + fallback pe classpath (unde erau imaginile vechi)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(
                        uploadsUri,                    // <proiect>/uploads/...
                        imagineUri,                    // <proiect>/imagine/... (dacă există)
                        "classpath:/static/uploads/",  // fallback pe classpath
                        "classpath:/static/imagine/"   // fallback pe classpath
                );

        registry.addResourceHandler("/imagine/**")
                .addResourceLocations(
                        imagineUri,
                        "classpath:/static/imagine/"
                );
    }
}