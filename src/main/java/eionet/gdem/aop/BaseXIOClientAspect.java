package eionet.gdem.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Aspect
@Component
public class BaseXIOClientAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseXIOClientAspect.class);

    @Around("execution(* org.basex.io.IOUrl.inputStream())")
    public Object sendRequest(ProceedingJoinPoint theProceedingJoinPoint) throws Throwable {
        Object res = null;
        try {
            res = theProceedingJoinPoint.proceed();
        } catch (IOException e) {
            LOGGER.error(String.valueOf(e));
        }
        LOGGER.info("test");
        System.out.println("test test test test test test test test test");
        return res;
    }
}
