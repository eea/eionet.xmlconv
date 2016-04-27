package eionet.gdem.qa.engines;

import eionet.gdem.GDEMException;
import eionet.gdem.Properties;
import eionet.gdem.qa.XQScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Executes xquery scripts using Client/Server BaseX architecture.
 * @author George Sofianos
 *
 */
public class BaseXServerImpl extends QAScriptEngineStrategy {

    private static final Logger logger = LoggerFactory.getLogger(BaseXServerImpl.class);

    @Override
    protected void runQuery(XQScript script, OutputStream result) throws GDEMException {
        try {
            int port = Integer.parseInt(Properties.basexServerPort);
            BaseXClient session = new BaseXClient(Properties.basexServerHost, port, Properties.basexServerUser, Properties.basexServerPassword);
            String input = script.getScriptSource();
            BaseXClient.Query query = session.query(input);
            query.bind("$source_url", script.getSrcFileUrl());
            while (query.more()) {
                result.write(query.next().getBytes());
            }
            logger.debug("Query: "+ query.info().getBytes());
            query.close();
            session.close();
        } catch (IOException e) {
            throw new GDEMException(e.getMessage(), e);
        } catch (NumberFormatException e) {
            throw new GDEMException("Wrong port number, please re-configure BaseX server connection parameters: " + e.getMessage(), e);
        }
    }
}
