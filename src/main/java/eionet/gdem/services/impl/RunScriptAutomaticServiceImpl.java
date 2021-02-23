package eionet.gdem.services.impl;

import eionet.gdem.XMLConvException;
import eionet.gdem.dcm.remote.RemoteService;
import eionet.gdem.qa.RunQAScriptMethod;
import eionet.gdem.services.RunScriptAutomaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Vector;

@Service("runScriptAutomaticService")
public class RunScriptAutomaticServiceImpl extends RemoteService implements RunScriptAutomaticService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RunScriptAutomaticServiceImpl.class);

    @Autowired
    public RunScriptAutomaticServiceImpl() {
        setTrustedMode(true);
    }

    /**
     * Remote method for running the QA script on the fly.
     *
     * @param sourceUrl URL of the source XML
     * @param scriptId XQueryScript ID or -1 (XML Schema validation) to be processed
     * @return Vector of 2 fields: content type and byte array
     * @throws XMLConvException in case of business logic error
     */
    @Override
    public Vector runQAScript(String sourceUrl, String scriptId) throws XMLConvException {

        if (!isHTTPRequest() && LOGGER.isDebugEnabled()) {
            LOGGER.debug("ConversionService.convert method called through XML-rpc.");
        }
        RunQAScriptMethod runQaMethod = new RunQAScriptMethod();
        setGlobalParameters(runQaMethod);
        return runQaMethod.runQAScript(sourceUrl, scriptId);

    }
}
