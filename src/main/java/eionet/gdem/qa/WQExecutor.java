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
 * The Original Code is XMLCONV - Conversion and QA Service
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by TripleDev or Zero Technologies are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        Enriko Käsper
 */

package eionet.gdem.qa;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eionet.gdem.Properties;

/**
 * WorkQueue executor class initiates the ThreadPool for running QA jobs.
 *
 * @author Enriko Käsper
 */
public final class WQExecutor {
    /** */
    private static final Log LOGGER = LogFactory.getLog(WQCheckerJob.class);

    private ExecutorService executor;
    private static WQExecutor wqExecutor;

    /**
     * Private default constructor
     */
    private WQExecutor() {
        executor = Executors.newFixedThreadPool(Properties.wqMaxJobs);
        LOGGER.debug("WQExecutor started");
    }

    /**
     * Returns Executor instance
     * @return Executor
     */
    public static WQExecutor getInstance() {
        if (wqExecutor == null) {
            wqExecutor = new WQExecutor();
        }
        return wqExecutor;
    }

    /**
     * Executes task
     * @param task Task to execute
     */
    public void execute(Runnable task){
        executor.execute(task);
    }

    /**
     * Shuts down executor.
     */
    public void shutdown(){
        executor.shutdown();
    }
}
