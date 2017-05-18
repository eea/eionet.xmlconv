package eionet.gdem.web.spring;

import eionet.gdem.Properties;
import eionet.gdem.XMLConvException;
import eionet.gdem.utils.SecurityUtil;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 *
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

    public LoginInterceptor() {
        super();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (!(handler instanceof ResourceHttpRequestHandler)) {
            String loginUrl = null;
            try {
                loginUrl = SecurityUtil.getLoginURL(request);
            } catch (XMLConvException e) {
                // do nothing
            }
            if (modelAndView.getViewName().contains("redirect")) {
                Map oldMap = RequestContextUtils.getInputFlashMap(request);
                FlashMap newMap = RequestContextUtils.getOutputFlashMap(request);
                if (oldMap != null) {
                    newMap.putAll(oldMap);
                }
                newMap.put("loginUrl", loginUrl);
            } else {
                modelAndView.addObject("loginUrl", loginUrl);
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        super.afterConcurrentHandlingStarted(request, response, handler);
    }
}
