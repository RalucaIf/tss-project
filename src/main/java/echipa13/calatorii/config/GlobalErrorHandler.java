package echipa13.calatorii.config;

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

}