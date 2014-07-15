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
 */

package eionet.gdem.web.struts.schema;

import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SchemaElemFormAction extends Action {

    /** */
    private static final Log LOGGER = LogFactory.getLog(SchemaElemFormAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        ActionMessages errors = new ActionMessages();
        SchemaElemForm form = (SchemaElemForm) actionForm;
        String schemaId = httpServletRequest.getParameter("schemaId");
        String user = (String) httpServletRequest.getSession().getAttribute("user");

        if (schemaId != null && schemaId != "") {
            httpServletRequest.getSession().setAttribute("schemaId", schemaId);
        } else {
            schemaId = (String) httpServletRequest.getSession().getAttribute("schemaId");
        }
        SchemaElemHolder seHolder = new SchemaElemHolder();

        try {
            SchemaManager sm = new SchemaManager();
            seHolder = sm.getSchemaElems(user, schemaId);
            if (seHolder == null || seHolder.getSchema() == null) {
                throw new DCMException(BusinessConstants.EXCEPTION_SCHEMA_NOT_EXIST);
            }
            form.setSchema(seHolder.getSchema().getSchema());
            form.setDescription(seHolder.getSchema().getDescription());
            form.setSchemaId(schemaId);
            form.setDtdId(seHolder.getSchema().getDtdPublicId());
            form.setElemName("");
            form.setNamespace("");
            form.setDoValidation(seHolder.getSchema().isDoValidation());
            form.setBlocker(seHolder.getSchema().isBlocker());
            form.setSchemaLang(seHolder.getSchema().getSchemaLang());
            form.setDtd(seHolder.getSchema().getIsDTD());
            String fileName = seHolder.getSchema().getUplSchemaFileName();
            form.setExpireDateObj(seHolder.getSchema().getExpireDate());
            if (seHolder.getSchema().getUplSchema() != null && !Utils.isNullStr(fileName)) {
                form.setUplSchemaId(seHolder.getSchema().getUplSchema().getUplSchemaId());
                form.setUplSchemaFileUrl(seHolder.getSchema().getUplSchema().getUplSchemaFileUrl());
                form.setLastModified(seHolder.getSchema().getUplSchema().getLastModified());
                form.setUplSchemaFileName(fileName);
                form.setUplSchemaFileUrl(Properties.gdemURL + "/schema/" + fileName);
            }
            seHolder.setSchemaIdRemoteUrl(Utils.isURL(seHolder.getSchema().getSchema())
                    && !seHolder.getSchema().getSchema().startsWith(SecurityUtil.getUrlWithContextPath(httpServletRequest)));
            httpServletRequest.getSession().setAttribute("schema.rootElements", seHolder);

        } catch (DCMException e) {
            // e.printStackTrace();
            LOGGER.error("Schema element form error", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
            saveErrors(httpServletRequest, errors);
        }

        httpServletRequest.getSession().setAttribute("stylesheet.outputtype", seHolder);
        return actionMapping.findForward("success");

    }
}
