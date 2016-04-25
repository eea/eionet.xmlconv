package eionet.gdem.qa.engines;

import eionet.gdem.GDEMException;
import eionet.gdem.qa.XQScript;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author George Sofianos
 *
 */
public class BaseXServerImpl extends QAScriptEngineStrategy {

    @Override
    protected void runQuery(XQScript script, OutputStream result) throws GDEMException {
        try {
            BaseXClient session = new BaseXClient("localhost", 32774, "admin", "admin");
            String input = script.getScriptSource();
            BaseXClient.Query query = session.query(input);
            query.bind("$source_url", script.getSrcFileUrl());
            while (query.more()) {
                result.write(query.next().getBytes());
            }
            result.write(query.info().getBytes());
            result.write(("Session info: " + session.info()).getBytes());
            session.close();
        } catch (IOException e) {
            throw new GDEMException(e.getMessage(), e);
        }
    }
}
