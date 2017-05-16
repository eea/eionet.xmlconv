package eionet.gdem.web.spring;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

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
}
