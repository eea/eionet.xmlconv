package eionet.gdem.web.struts.qasandbox;

import eionet.gdem.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author George Sofianos
 *
 */
@WebServlet(value="/qasandbox/action")
public class TmpUploadAction extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(TmpUploadAction.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String command = req.getParameter("command");
        HttpSession session = req.getSession();
        String tmpdir = Properties.appRootFolder + File.separator + "tmpfile";
        String sessionDir = tmpdir + File.separator + session.getId();
        resp.setContentType("application/json");
        if ("getFiles".equals(command)) {
            try (Stream<Path> stream = Files.walk(Paths.get(sessionDir))) {
                List<File> files = stream
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .collect(Collectors.toList());
                int index = 0;
                int all = files.size();
                String test = "{ \"Data\": [";
                for (File file : files) {
                    String fileName = file.getName();
                    String outputUrl = "http://" + Properties.appHost + ("".equals(req.getContextPath()) ? "" : req.getContextPath()) + "/tmpfile/" + session.getId() + "/" + fileName;
                    test = test + "{ \"name\": \"" + fileName + "\", \"size\": \"" + file.length() + "\", \"url\": \"" + outputUrl + "\"}";
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
        } else if ("deleteFile".equals(command)) {
            String fileName = req.getParameter("filename");
            if (fileName != null) {
                try (Stream<Path> stream = Files.walk(Paths.get(sessionDir))) {
                    List<File> files = stream
                            .filter(Files::isRegularFile)
                            .map(Path::toFile)
                            .collect(Collectors.toList());
                    for (File file : files) {
                        String localName = file.getName();
                        if (localName != null) {
                            if (localName.equals(fileName)) {
                                LOGGER.info("Deleting temporary file: " + localName + " for SessionId: " + session.getId());
                                Files.deleteIfExists(Paths.get(file.getPath()));
                            }
                        }
                    }
                    resp.setStatus(HttpServletResponse.SC_OK);
                } catch (Exception e) {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                } finally {
                    PrintWriter out = resp.getWriter();
                    out.write("{}");
                    out.close();
                }
            }
        }
    }
}
