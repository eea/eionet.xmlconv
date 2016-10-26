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
 * The Original Code is XMLCONV - Converters and QA Services
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Zero Technologies or TripleDev are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s): Enriko Käsper, TripleDev
 */
package eionet.gdem.web.job;

import java.util.List;


import eionet.gdem.cache.CacheManagerUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import eionet.gdem.dcm.business.DDServiceClient;
import eionet.gdem.dto.DDDatasetTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Job fills Data Dictionary dataset tables cache.
 *
 * @author Enriko Käsper, TripleDev
 */
public class DDTablesCacheUpdater implements Job {

    /**
     * Class internal logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(DDTablesCacheUpdater.class);

    /**
     * Executes the job.
     *
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     * @param context
     *            current context.
     * @throws JobExecutionException
     *             if execution fails.
     */
    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {

        try {
            List<DDDatasetTable> ddTables = DDServiceClient.getDDTablesFromDD();

            CacheManagerUtil.updateDDTablesCache(ddTables);
            logger.debug("DD tables cache updated");
        } catch (Exception e) {
            logger.error("Error when updating DD tables cache: ", e);
        }
    }

}
