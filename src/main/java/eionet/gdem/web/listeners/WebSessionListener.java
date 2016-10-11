package eionet.gdem.web.listeners;

import eionet.gdem.Properties;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.File;
import java.io.FilenameFilter;

/**
 * Session listener.
 * @author George Sofianos
 */
@WebListener
public class WebSessionListener implements HttpSessionListener {
    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        String id = httpSessionEvent.getSession().getId();
        String sessionDir = Properties.appRootFolder + "/tmpfile/" + id;
        File dir = new File(sessionDir);
        for(File file: dir.listFiles()) {
            file.delete();
        }
    }
}
