package eionet.gdem.qa.engines;

import eionet.gdem.XMLConvException;
import eionet.gdem.Properties;
import eionet.gdem.qa.XQScript;
import eionet.gdem.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Executes XQuery scripts using Client/Server BaseX architecture.
 * @author George Sofianos
 *
 */
public class BaseXServerImpl extends QAScriptEngineStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseXServerImpl.class);

    @Override
    protected void runQuery(XQScript script, OutputStream result) throws XMLConvException {
        try {
            int port = Integer.parseInt(Properties.basexServerPort);
            String input = null;
            if (!Utils.isNullStr(script.getScriptSource())) {
                input = script.getScriptSource();
            } else if (!Utils.isNullStr(script.getScriptFileName())) {
                try (Reader queryReader = new FileReader(script.getScriptFileName())) {
                    input = new String(IOUtils.toByteArray(queryReader, "UTF-8"));
                } catch (IOException e) {
                    LOGGER.error("Error while reading XQuery file: " + e);
                    throw new XMLConvException("Error while reading XQuery file: " + script.getScriptFileName() + " : " + ExceptionUtils.getStackTrace(e), e);
                }
            }
            try (BaseXClient session = new BaseXClient(Properties.basexServerHost, port, Properties.basexServerUser, Properties.basexServerPassword)) {
                try (BaseXClient.Query query = session.query(input)) {
                    query.bind("$source_url", script.getSrcFileUrl());
                    query.bind("$base_url", Properties.gdemURL + Properties.contextPath, "xs:string");
                    while (query.more()) {
                        result.write(query.next().getBytes(StandardCharsets.UTF_8));
                    }
                    LOGGER.debug("Query: " + query.info());
                } catch (IOException e) {
                    throw new XMLConvException("Error while executing XQuery script: " + script.getScriptFileName() + " : " + ExceptionUtils.getStackTrace(e), e);
                }
            } catch (IOException e) {
                LOGGER.error("Error connecting to BaseX server: " + e);
                throw new XMLConvException("Error while connecting to BaseX server.", e);
            }
        } catch (NumberFormatException e) {
            throw new XMLConvException("Wrong port number, please re-configure BaseX server connection parameters: " + e.getMessage(), e);
        }
    }
}