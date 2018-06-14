package eionet.gdem.web.spring.interceptors;

import org.springframework.http.CacheControl;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * This is unused for now. The purpose is to have a default cache control for non-static resources.
 * TODO: check if not needed and remove.
 */
public class StaticCacheInterceptor extends WebContentInterceptor {


    public StaticCacheInterceptor() {
        super();
        setCacheControl(CacheControl.maxAge(0L, TimeUnit.SECONDS).noCache().cachePublic().mustRevalidate());
    }
}
