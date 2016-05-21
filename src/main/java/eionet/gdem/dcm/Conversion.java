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
 *    Original code: Nedeljko Pavlovic (ED)
 */

package eionet.gdem.dcm;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;




import eionet.gdem.Properties;
import eionet.gdem.dto.ConversionDto;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.utils.xml.IXQuery;
import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.XmlContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Conversion class.
 */
public class Conversion {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(Conversion.class);
    public static String CONVERSION_ELEMENT = "conversion";
    /** List of generated conversions. */
    private static List<ConversionDto> conversions = new ArrayList<ConversionDto>();

    /**
     * Load generated conversion types from dcm/conversions.xml.
     */
    static {
        try {

            IXmlCtx ctx = new XmlContext();
            ctx.checkFromFile(Properties.convFile);
            IXQuery xQuery = ctx.getQueryManager();
            List<String> identifiers = xQuery.getElementIdentifiers(CONVERSION_ELEMENT);
            for (int i = 0; i < identifiers.size(); i++) {
                String id = identifiers.get(i);
                ConversionDto resObject = new ConversionDto();
                resObject.setConvId(id);
                resObject.setDescription(xQuery.getElementValue(id, "description"));
                resObject.setResultType(xQuery.getElementValue(id, "result_type"));
                resObject.setStylesheet(xQuery.getElementValue(id, "stylesheet"));
                resObject.setIgnoreGeneratedIfManualExists(xQuery.getElementValue(id, "ignore_if_manual") != null
                        && "true".equals(xQuery.getElementValue(id, "ignore_if_manual")));
                Hashtable convType = GDEMServices.getDaoService().getConvTypeDao().getConvType(resObject.getResultType());

                resObject.setContentType((String) convType.get("content_type"));
                conversions.add(resObject);
            }
        } catch (Exception ex) {
            LOGGER.error("Error reading conversions.xml file ", ex);
        }

    }

    /**
     * Get the list of generated conversion types.
     *
     * @return
     */
    public static List<ConversionDto> getConversions() {
        return conversions;
    }

    /**
     * Get generated conversion type by ID.
     *
     * @param convId
     *            Conversion ID stored in xml conf.
     * @return ConversionDto object.
     */
    public static ConversionDto getConversionById(String convId) {
        ConversionDto conversion = null;
        for (int i = 0; i < conversions.size(); i++) {
            if ((conversions.get(i)).getConvId().compareTo(convId) == 0) {
                conversion = conversions.get(i);
                break;
            }
        }
        return conversion;
    }

}
