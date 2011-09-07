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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.WorkqueueJob;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.qa.XQueryService;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.services.db.dao.IXQJobDao;
import eionet.gdem.utils.SecurityUtil;

/**
 * WorkqueueManager
 * 
 * @author Enriko Käsper, Tieto Estonia
 */

public class WorkqueueManager {

    private static LoggerIF _logger = GDEMServices.getLogger();
    private IXQJobDao jobDao = GDEMServices.getDaoService().getXQJobDao();

    public WorkqueueJob getWqJob(String jobId) throws DCMException {
        WorkqueueJob job = null;
        try {
            String[] jobData = jobDao.getXQJobData(jobId);
            if (jobData != null && jobData.length > 4) {
                job = new WorkqueueJob();
                job.setUrl(jobData[0]);
                job.setScriptFile(jobData[1]);
                job.setResultFile(jobData[2]);
                job.setStatus(new Integer(jobData[3]));
                job.setSrcFile(jobData[4]);
                job.setScriptId(jobData[5]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _logger.error("Error getting workqueue job", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return job;

    }

    /**
     * Adds a new jobs into the workqueue using script content sent as the method parameter
     * 
     * @param user
     * @param sourceUrl
     * @param scriptContent
     * @param scriptType
     * @return
     * @throws DCMException
     */
    public String addQAScriptToWorkqueue(String user, String sourceUrl, String scriptContent, String scriptType)
            throws DCMException {

        try {
            if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_WQ_PATH, "i")) {
                _logger.debug("You don't have permissions jobs into workqueue!");
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_QASCRIPT_UPDATE);
            }

        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            _logger.error("Error adding job to workqueue", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        XQueryService xqE = new XQueryService();
        xqE.setTrustedMode(false);
        try {
            String result = xqE.analyze(sourceUrl, scriptContent, scriptType);
            return result;
        } catch (Exception e) {
            _logger.error("Error adding job to workqueue", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }

    /**
     * Adds new jobs into the workqueue by the goven XML Schema
     * 
     * @param user
     * @param sourceUrl
     * @param schemaUrl
     * @return
     * @throws DCMException
     */
    public List<String> addSchemaScriptsToWorkqueue(String user, String sourceUrl, String schemaUrl) throws DCMException {

        List<String> result = new ArrayList<String>();
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_WQ_PATH, "i")) {
                _logger.debug("You don't have permissions jobs into workqueue!");
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_QASCRIPT_UPDATE);
            }

        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            _logger.error("Error adding job to workqueue", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        XQueryService xqE = new XQueryService();
        xqE.setTrustedMode(false);
        try {
            Hashtable h = new Hashtable();
            Vector files = new Vector();
            files.add(sourceUrl);
            h.put(schemaUrl, files);
            Vector v_result = xqE.analyzeXMLFiles(h);
            if (v_result != null) {
                for (int i = 0; i < v_result.size(); i++) {
                    Vector v = (Vector) v_result.get(i);
                    result.add((String) v.get(0));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            _logger.error("Error adding job to workqueue", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return result;
    }
}
