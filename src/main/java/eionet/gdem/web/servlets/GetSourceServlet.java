package eionet.gdem.web.servlets;

import eionet.gdem.Constants;
import eionet.gdem.http.HttpFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author George Sofianos
 */
@WebServlet("/s/getSource")
public class GetSourceServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetSourceServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ticket = req.getParameter(Constants.TICKET_PARAM);
        String url = req.getParameter(Constants.SOURCE_URL_PARAM);
        try {
            HttpFileManager manager = new HttpFileManager();
            manager.getHttpResponse(resp, ticket, url);
        } catch (Exception e) {
            LOGGER.error("Error: " + e);
        }
    }
}
