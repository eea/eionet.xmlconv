package eionet.gdem.web.spring;

import eionet.gdem.exceptions.PathNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.MissingResourceException;

/**
 *
 *
 */
@ControllerAdvice(basePackages = {"eionet.gdem.web"})
public class ExceptionHandlerAdvice {

    private final String EXCEPTION_STATUS = "status";
    private final String EXCEPTION_MESSAGE = "exceptionMessage";

    @ExceptionHandler(value = {RuntimeException.class})
    public ModelAndView exception(Exception exception) {
        ModelAndView modelAndView = new ModelAndView("Error");
        modelAndView.addObject(EXCEPTION_STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        modelAndView.addObject(EXCEPTION_MESSAGE, exception.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView otherExceptions(Exception exception) {
        ModelAndView modelAndView = new ModelAndView("Error");
        modelAndView.addObject(EXCEPTION_STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        modelAndView.addObject(EXCEPTION_MESSAGE, exception.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(MissingResourceException.class)
    public ModelAndView otherrr(Exception exception) {
        ModelAndView modelAndView = new ModelAndView("Error");
        modelAndView.addObject(EXCEPTION_STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        modelAndView.addObject(EXCEPTION_MESSAGE, exception.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(BindException.class)
    public ModelAndView bindException(Exception exception) {
        ModelAndView modelAndView = new ModelAndView("Error");
        modelAndView.addObject(EXCEPTION_STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        modelAndView.addObject(EXCEPTION_MESSAGE, exception.getMessage());
        return modelAndView;
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView accessDenied(Exception exception) {
        ModelAndView modelAndView = new ModelAndView("Error");
        modelAndView.setStatus(HttpStatus.UNAUTHORIZED);
        modelAndView.addObject(EXCEPTION_STATUS, HttpStatus.UNAUTHORIZED.value());
        modelAndView.addObject(EXCEPTION_MESSAGE, exception.getMessage());
        return modelAndView;
    }
    @ExceptionHandler(PathNotFoundException.class)
    public ModelAndView pathNotFound(Exception exception) {
        ModelAndView modelAndView = new ModelAndView("Error");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        modelAndView.addObject(EXCEPTION_STATUS, HttpStatus.NOT_FOUND.value());
        modelAndView.addObject(EXCEPTION_MESSAGE, exception.getMessage());
        return modelAndView;
    }
}
