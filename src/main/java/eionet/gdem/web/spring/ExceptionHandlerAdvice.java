package eionet.gdem.web.spring;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.MissingResourceException;

/**
 *
 *
 */
@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(value = {RuntimeException.class})
    public ModelAndView exception(Exception exception, WebRequest request) {
        ModelAndView modelAndView = new ModelAndView("Error");
        modelAndView.addObject("exceptionMessage", exception.getCause());
        return modelAndView;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView otherExceptions(Exception exception) {
        ModelAndView modelAndView = new ModelAndView("Error");
        modelAndView.addObject("exceptionMessage", exception.getCause());
        return modelAndView;
    }

    @ExceptionHandler(MissingResourceException.class)
    public ModelAndView otherrr(Exception exception) {
        ModelAndView modelAndView = new ModelAndView("Error");
        modelAndView.addObject("exceptionMessage", exception.getCause());
        return modelAndView;
    }
}
