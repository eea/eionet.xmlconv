package eionet.gdem.web.listeners;

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
        /*HttpSession session = httpSessionEvent.getSession();
        List tmpFiles = (ArrayList) session.getAttribute("tmpUpload");
        for (int i = 0; i <= tmpFiles.size(); i++) {
            File tmp = new File((String) tmpFiles.get(i));
            tmp.delete();
        }*/
        String id = httpSessionEvent.getSession().getId();
        File dir = new File(System.getProperty("java.io.tmpdir"));
        FilenameFilter filter = (dir1, name) -> {
            if (name.contains(id)) return true;
            return false;
        };
        for(File file: dir.listFiles(filter)) {
            file.delete();
        }
    }
}
