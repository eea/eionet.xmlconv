package eionet.gdem.web.struts.qasandbox;

import eionet.gdem.Properties;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author George Sofianos
 *
 */
@WebServlet(value="/qasandbox/getFiles")
public class TmpUploadGetFilesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String tmpdir = Properties.appRootFolder + File.separator + "tmpfile";
        String sessionDir = tmpdir + File.separator + session.getId();
        DecimalFormat df = new DecimalFormat("0.00");
        resp.setContentType("application/json");
        try {
            List<File> files = Files.walk(Paths.get(sessionDir))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
            int index = 0;
            int all = files.size();
            String test = "{ \"Data\": [";
            for (File file : files) {
                String fileName = file.getName();
                String outputUrl = "http://" + Properties.appHost + ("".equals(req.getContextPath()) ? "" : File.separator + req.getContextPath()) + File.separator + "tmpfile" + File.separator + session.getId() + File.separator + fileName;
                test = test + "{ \"name\": \"" + fileName + "\", \"size\": \"" + df.format(file.length() / 1024.0f / 1024.0f) + "\", \"url\": \"" + outputUrl + "\"}";
                index++;
                if (index != all) {
                    test = test + ",";
                }
            }
            test = test + "]}";
            PrintWriter out = resp.getWriter();
            out.write(test);
            out.close();
        } catch (NoSuchFileException ex) {
            String test = "{ \"Data\": []}";
            PrintWriter out = resp.getWriter();
            out.write(test);
            out.close();
        }
    }
}
