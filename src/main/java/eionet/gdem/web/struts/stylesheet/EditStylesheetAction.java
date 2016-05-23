/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Web Dashboards Service
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 *
 * Contributors(s):
 *    Original code: Istvan Alfeldi (ED)
 *    Contributor(s): Enriko Käsper (TripleDev)
 */

package eionet.gdem.web.struts.stylesheet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.RedirectingActionForward;
import org.apache.struts.actions.LookupDispatchAction;
import org.apache.struts.upload.FormFile;

import com.mysql.jdbc.StringUtils;

import eionet.gdem.Constants;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.business.StylesheetManager;
import eionet.gdem.dto.Stylesheet;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Action for editing stylehseet data and uploading XSL file.
 *
 * @author Enriko Käsper
 */
public class EditStylesheetAction extends LookupDispatchAction {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(EditStylesheetAction.class);

    /**
     * The method uploads XSL file from user's filesystem to the repository. Saves all the other changes made on the form except the
     * file source in textarea.
     * @param actionMapping Struts ActionMapping
     * @param actionForm StylesheetForm
     * @param httpServletRequest HTTP servlet request
     * @param httpServletResponse HTTP servlet response
     * @return ActionForward
     */
    public ActionForward upload(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();

        StylesheetForm form = (StylesheetForm) actionForm;
        Stylesheet stylesheet = AddEditStylehseetUtils.convertFormToStylesheetDto(form, httpServletRequest);

        FormFile xslFile = form.getXslfile();
        String user = (String) httpServletRequest.getSession().getAttribute("user");
        boolean updateContent = false;

        httpServletRequest.setAttribute("stylesheetId", stylesheet.getConvId());

        if (isCancelled(httpServletRequest)) {
            return findForward(actionMapping, "success", stylesheet.getConvId());
        }
        String description = form.getDescription();
        if (description == null || description.isEmpty()) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.stylesheet.error.descriptionMissing"));
        }
        if (xslFile != null && xslFile.getFileSize() != 0) {
            if (StringUtils.isNullOrEmpty(stylesheet.getXslFileName())) {
                stylesheet.setXslFileName(xslFile.getFileName());
            }
            try {
                stylesheet.setXslContent(new String(xslFile.getFileData(), "UTF-8"));
            } catch (Exception e) {
                LOGGER.error("Error in edit stylesheet action when trying to load XSL file content from FormFile object", e);
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(BusinessConstants.EXCEPTION_GENERAL));
            } finally {
                xslFile.destroy();
            }
            AddEditStylehseetUtils.validateXslFile(stylesheet, errors);
            updateContent = true;
        }

        if (errors.isEmpty()) {
            try {
                StylesheetManager stylesheetManager = new StylesheetManager();
                // stylesheetManager.update(user, stylesheetId, schema, xslFile, curFileName, type, desc, dependsOn);
                stylesheetManager.update(stylesheet, user, updateContent);
                if (updateContent) {
                    messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.stylesheet.updated"));
                } else {
                    messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.stylesheet.updated.notuploaded"));
                }
                StylesheetListLoader.reloadStylesheetList(httpServletRequest);
            } catch (DCMException e) {
                LOGGER.error("Edit stylesheet error", e);
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
            }
        }

        if (!errors.isEmpty()) {
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("fail");
        }
        httpServletRequest.getSession().setAttribute("dcm.messages", messages);
        return findForward(actionMapping, "success", stylesheet.getConvId());
    }

    /**
    * The method saves all the changes made on the form. Saves also modifications made to the file source textarea.
    * @param actionMapping Struts ActionMapping
    * @param actionForm StylesheetForm
    * @param httpServletRequest HTTP servlet request
    * @param httpServletResponse HTTP servlet response
    * @return ActionForward
    */
    public ActionForward save(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();

        StylesheetForm form = (StylesheetForm) actionForm;
        Stylesheet stylesheet = AddEditStylehseetUtils.convertFormToStylesheetDto(form, httpServletRequest);

        String user = (String) httpServletRequest.getSession().getAttribute("user");
        String oldFileChecksum = form.getChecksum();
        boolean updateContent = false;
        String newChecksum = null;

        httpServletRequest.setAttribute("stylesheetId", stylesheet.getConvId());

        if (isCancelled(httpServletRequest)) {
            return findForward(actionMapping, "success", stylesheet.getConvId());
        }
        String description = form.getDescription();
        if (description == null || description.isEmpty()) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.stylesheet.error.descriptionMissing"));
        }
        if (!Utils.isNullStr(stylesheet.getXslFileName()) && !Utils.isNullStr(stylesheet.getXslContent())
                && stylesheet.getXslContent().indexOf(Constants.FILEREAD_EXCEPTION) == -1) {

            // compare checksums
            try {
                newChecksum = Utils.getChecksumFromString(stylesheet.getXslContent());
            } catch (Exception e) {
                LOGGER.error("unable to create checksum");
            }

            updateContent = StringUtils.isNullOrEmpty(oldFileChecksum) || !oldFileChecksum.equals(newChecksum);

            if (updateContent) {
                AddEditStylehseetUtils.validateXslFile(stylesheet, errors);
            }
        }

        if (errors.isEmpty()) {
            try {
                StylesheetManager stylesheetManager = new StylesheetManager();
                // st.updateContent(user, stylesheetId, schema, xslFileName, type, desc, xslContent, updateContent, dependsOn);
                stylesheetManager.update(stylesheet, user, updateContent);
                messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.stylesheet.updated"));
                StylesheetListLoader.reloadStylesheetList(httpServletRequest);
            } catch (DCMException e) {
                LOGGER.error("Edit stylesheet error", e);
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
            }
        }

        if (!errors.isEmpty()) {
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("fail");
        }
        httpServletRequest.getSession().setAttribute("dcm.messages", messages);

        return findForward(actionMapping, "success", stylesheet.getConvId());
    }

    /**
     * Cancel edit action and return to view mode.
     * @param actionMapping Struts ActionMapping
     * @param actionForm StylesheetForm
     * @param httpServletRequest HTTP servlet request
     * @param httpServletResponse HTTP servlet response
     * @return ActionForward
     */
    public ActionForward cancel(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        return actionMapping.findForward("success");
    }

    @Override
    protected Map<String, String> getKeyMethodMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("label.stylesheet.save", "save");
        map.put("label.stylesheet.upload", "upload");
        return map;
    }

    /**
     * Returns the redirect path.
     * @param actionMapping Action mapping
     * @param f F
     * @param stylesheetId Stylesheet Id
     * @return Action forward
     */
    private ActionForward findForward(ActionMapping actionMapping, String f, String stylesheetId) {
        ActionForward forward = actionMapping.findForward(f);
        StringBuffer path = new StringBuffer(forward.getPath());
        path.append("?stylesheetId=" + stylesheetId);
        forward = new RedirectingActionForward(path.toString());
        return forward;
    }
}
