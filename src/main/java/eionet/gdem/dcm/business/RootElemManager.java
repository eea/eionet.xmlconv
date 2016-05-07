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

package eionet.gdem.dcm.business;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.IRootElemDao;
import eionet.gdem.utils.SecurityUtil;

/**
 * Root element manager.
 * @author Unknown
 * @author George Sofianos
 */
public class RootElemManager {

    /** */
    private static final Log LOGGER = LogFactory.getLog(RootElemManager.class);

    private IRootElemDao rootElemDao = GDEMServices.getDaoService().getRootElemDao();

    /**
     * Delete element
     * @param user User
     * @param elemId Element id
     * @throws DCMException If an error occurs.
     */
    public void delete(String user, String elemId) throws DCMException {

        try {
            if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_SCHEMA_PATH, "d")) {
                LOGGER.debug("You don't have permissions to delete root element!");
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_ELEMENT_DELETE);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.debug(e.toString(), e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        // StringBuffer err_buf = new StringBuffer();
        // String del_id= (String)req.getParameter(Names.XSD_DEL_ID);

        try {
            rootElemDao.removeRootElem(elemId);
        } catch (Exception e) {
            LOGGER.debug(e.toString(), e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
    }

    /**
     * Adds element
     * @param user User
     * @param schemaId Schema id
     * @param elemName Element id
     * @param namespace Namespace
     * @throws DCMException If an error occurs.
     */
    public void add(String user, String schemaId, String elemName, String namespace) throws DCMException {

        try {
            if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_SCHEMA_PATH, "i")) {
                LOGGER.debug("You don't have permissions to insert root elements!");
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_ELEMENT_INSERT);
            }
        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.debug(e.toString(), e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

        try {
            rootElemDao.addRootElem(schemaId, elemName, namespace);
        } catch (Exception e) {
            LOGGER.debug(e.toString(), e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
    }

}
