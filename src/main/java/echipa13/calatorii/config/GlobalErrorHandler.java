package echipa13.calatorii.config;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class GlobalErrorHandler {

    // 404
    @ExceptionHandler(NoHandlerFoundException.class)
    public String handle404(NoHandlerFoundException ex) {
        return "error/404";
    }

    // 403
    @ExceptionHandler(AccessDeniedException.class)
    public String handle403(AccessDeniedException ex) {
        return "error/403";
    }

//    // 500
//    @ExceptionHandler(Exception.class)
//    public String handle500(Exception ex) {
//        return "error/500";
//    }

    private static final Logger log = LoggerFactory.getLogger(GlobalErrorHandler.class);

    // WHY: evităm randarea paginii de eroare după commit; facem redirect curat
    @ExceptionHandler(IllegalArgumentException.class)
    public String illegalArg(IllegalArgumentException ex, HttpServletResponse resp) {
        log.warn("Redirect /journal din cauza: {}", ex.getMessage());
        resp.setStatus(302);
        return "redirect:/journal";
    }
}