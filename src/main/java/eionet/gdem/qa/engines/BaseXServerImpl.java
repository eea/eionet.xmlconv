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
            BaseXClient session = null;
            try {
                session = new BaseXClient(Properties.basexServerHost, port, Properties.basexServerUser, Properties.basexServerPassword);
            } catch (IOException e) {
                throw new XMLConvException("Error while connecting to BaseX server.", e);
            }
            String input = null;
            if (!Utils.isNullStr(script.getScriptSource())) {
                input = script.getScriptSource();
            } else if (!Utils.isNullStr(script.getScriptFileName())) {
                try {
                    queryReader = new FileReader(script.getScriptFileName());
                    input = new String(IOUtils.toByteArray(queryReader, "UTF-8"));
                } catch (FileNotFoundException e) {
                    throw new XMLConvException("Error while reading XQuery file: " + script.getScriptFileName(), e);
                } catch (IOException e) {
                    throw new XMLConvException("Error while reading XQuery file: " + script.getScriptFileName(), e);
                }
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
        }  catch (NumberFormatException e) {
            throw new XMLConvException("Wrong port number, please re-configure BaseX server connection parameters: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new XMLConvException("Error while executing XQuery script: " + script.getScriptFileName(), e);
        } finally {
            try {
                if (queryReader != null) {
                    queryReader.close();
                }
            } catch (IOException e) {
                // do nothing
            }
        }
    }
}
