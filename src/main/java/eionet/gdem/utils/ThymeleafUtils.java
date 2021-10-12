package eionet.gdem.utils;

import eionet.acl.AppUser;
import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.XMLConvException;
import eionet.gdem.web.tags.breadcrumbs.BreadCrumb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public final class ThymeleafUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThymeleafUtils.class);

    public ThymeleafUtils() {
    }

    /**
     * Set up Header variables
     * @param model
     * @param title
     * @param request
     * @return model
     */
    public static Model setUpTitleAndLogin(Model model, String title, HttpServletRequest request) {
        //Add page title
        model.addAttribute("title", title);

        //Add loginUrl param
        try {
            model.addAttribute("loginUrl", SecurityUtil.getLoginURL(request));
        } catch (XMLConvException e) {
            LOGGER.error("Could not retrieve login url");
        }
        //Add user param
        AppUser user =  SecurityUtil.getUser(request, Constants.USER_ATT);
        if(user != null){
            model.addAttribute("username", user.getUserName());
        }

        return model;
    }

    /**
     * Set up breadcrumbs
     * @param model
     * @return model
     */
    public static Model setUpBreadCrumbsForScriptExecutionHistory(Model model, String scriptId) {
        List<BreadCrumb> breadcrumbs = new ArrayList();
        breadcrumbs.add(new BreadCrumb(Properties.gdemURL + "/index.jsp", Properties.getStringProperty("label.gdem.title")));
        breadcrumbs.add(new BreadCrumb(Properties.gdemURL + "/scripts", Properties.getStringProperty("label.qascript.all.title")));
        breadcrumbs.add(new BreadCrumb(Properties.gdemURL + "/scripts/" + scriptId, Properties.getStringProperty("label.qascriptView.title")));
        breadcrumbs.add(new BreadCrumb(null, Properties.getStringProperty("label.qascript.executionHistory.title")));

        model.addAttribute("breadcrumbs", breadcrumbs);
        return model;
    }
}
