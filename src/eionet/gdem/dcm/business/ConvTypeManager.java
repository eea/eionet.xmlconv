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
 * The Original Code is XMLCONV.
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 * Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.dcm.business;

import java.util.Hashtable;

import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.ConvType;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.services.db.dao.IConvTypeDao;

/**
 * ConvTypeManager
 *
 * @author Enriko Käsper, Tieto Estonia
 */

public class ConvTypeManager {

    private static LoggerIF _logger = GDEMServices.getLogger();
    private IConvTypeDao convTypeDao = GDEMServices.getDaoService().getConvTypeDao();

    /**
     * Get conversion type mappings
     *
     * @param convTypeId
     *            (HTML, XML, ...)
     * @return
     * @throws DCMException
     */
    public ConvType getConvType(String convTypeId) throws DCMException {
        ConvType convType = null;
        try {

            Hashtable type = convTypeDao.getConvType(convTypeId);
            if (type == null)
                return null;
            convType = new ConvType();
            convType.setContType(type.get("content_type") == null ? null : (String) type.get("content_type"));
            convType.setConvType(type.get("conv_type") == null ? null : (String) type.get("conv_type"));
            convType.setDescription(type.get("description") == null ? null : (String) type.get("description"));
            convType.setFileExt(type.get("file_ext") == null ? null : (String) type.get("file_ext"));

        } catch (Exception e) {
            e.printStackTrace();
            _logger.error("Error getting conv types", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return convType;

    }

}
