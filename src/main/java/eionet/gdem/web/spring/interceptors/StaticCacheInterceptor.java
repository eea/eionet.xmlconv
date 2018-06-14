package eionet.gdem.web.spring.interceptors;

import org.springframework.http.CacheControl;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class StaticCacheInterceptor extends WebContentInterceptor {


    public StaticCacheInterceptor() {
        super();
        setCacheControl(CacheControl.maxAge(0L, TimeUnit.SECONDS).noCache().cachePublic().mustRevalidate());
        Properties props = new Properties();
        props.setProperty("/css/**", "172800");
        props.setProperty("/js/**", "172800");
        props.setProperty("/fonts/**", "172800");
        props.setProperty("/images/**", "172800");
        setCacheMappings(props);
    }
}
