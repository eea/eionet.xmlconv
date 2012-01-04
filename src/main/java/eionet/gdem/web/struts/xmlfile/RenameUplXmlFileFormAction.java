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
 * The Original Code is Content Registry 3
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by TripleDev or Zero Technologies are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        Juhan Voolaid
 */

package eionet.gdem.web.struts.xmlfile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.dcm.business.UplXmlFileManager;
import eionet.gdem.dto.UplXmlFile;
import eionet.gdem.exceptions.DCMException;

/**
 * Action for setting up rename form of the selected xml file.
 *
 * @author Juhan Voolaid
 */
public class RenameUplXmlFileFormAction extends Action {

    /** */
    private static final Log LOGGER = LogFactory.getLog(RenameUplXmlFileFormAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        ActionMessages errors = new ActionMessages();

        EditUplXmlFileForm form = (EditUplXmlFileForm) actionForm;
        String xmlfileId = httpServletRequest.getParameter("xmlfileId");
        LOGGER.debug("ID: " + xmlfileId);

        if (StringUtils.isEmpty(xmlfileId)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.uplXmlFile.error.notSelected"));
            httpServletRequest.getSession().setAttribute("dcm.errors", errors);
            return actionMapping.findForward("fail");
        }

        try {
            UplXmlFileManager fm = new UplXmlFileManager();
            UplXmlFile xmlfile = fm.getUplXmlFileById(xmlfileId);

            form.setXmlfileId(xmlfile.getId());
            form.setXmlFileName(xmlfile.getFileName());
            form.setXmlFilePath(xmlfile.getFileName());

            form.setTitle(xmlfile.getTitle());
            form.setLastModified(xmlfile.getLastModified());

        } catch (DCMException e) {
            LOGGER.error("Error editing uploaded XML file", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("fail");
        }

        return actionMapping.findForward("success");
    }
}
