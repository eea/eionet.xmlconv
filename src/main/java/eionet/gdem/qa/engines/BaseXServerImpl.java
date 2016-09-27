package eionet.gdem.qa.engines;

import eionet.gdem.XMLConvException;
import eionet.gdem.Properties;
import eionet.gdem.qa.XQScript;
import eionet.gdem.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Executes XQuery scripts using Client/Server BaseX architecture.
 * @author George Sofianos
 *
 */
public class BaseXServerImpl extends QAScriptEngineStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseXServerImpl.class);

    @Override
    protected void runQuery(XQScript script, OutputStream result) throws XMLConvException {
        Reader queryReader = null;
        try {
            int port = Integer.parseInt(Properties.basexServerPort);
            BaseXClient session = new BaseXClient(Properties.basexServerHost, port, Properties.basexServerUser, Properties.basexServerPassword);
            String input = null;
            if (!Utils.isNullStr(script.getScriptSource())) {
                input = script.getScriptSource();
            } else if (!Utils.isNullStr(script.getScriptFileName())) {
                queryReader = new FileReader(script.getScriptFileName());
                input = new String(IOUtils.toByteArray(queryReader, "UTF-8"));
            }
            BaseXClient.Query query = session.query(input);
            query.bind("$source_url", script.getSrcFileUrl());
            query.bind("$base_url", "http://" + Properties.appHost + Properties.contextPath);
            while (query.more()) {
                result.write(query.next().getBytes());
            }
            LOGGER.debug("Query: "+ query.info().getBytes());
            query.close();
            session.close();
        } catch (IOException e) {
            throw new XMLConvException(e.getMessage(), e);
        } catch (NumberFormatException e) {
            throw new XMLConvException("Wrong port number, please re-configure BaseX server connection parameters: " + e.getMessage(), e);
        } finally {
            try {
                if (queryReader != null) {
                    queryReader.close();
                }
            } catch (IOException e) {
                LOGGER.error("Error while reading xquery file: " + e.getMessage());
            }
        }
    }
}
