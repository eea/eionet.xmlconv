package eionet.gdem.qa.engines;

import eionet.gdem.GDEMException;
import eionet.gdem.qa.XQScript;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.MainOptions;
import org.basex.core.cmd.Set;
import org.basex.query.QueryException;
import org.basex.query.QueryProcessor;
import org.basex.query.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author George Sofianos
 *
 */
public class BaseXLocalImpl extends QAScriptEngineStrategy {

    private static final Logger logger = LoggerFactory.getLogger(BaseXLocalImpl.class);

    @Override
    protected void runQuery(XQScript script, OutputStream result) throws GDEMException {

        Context context = new Context();
        try {
            new Set(MainOptions.INTPARSE, true).execute(context);
            QueryProcessor proc = new QueryProcessor(script.getScriptSource(), context);
            proc.bind("source_url", script.getOrigFileUrl());
            Value res = proc.value();
            result.write(res.toString().getBytes());
            proc.close();
            logger.info("proc info: " + proc.info());
            logger.info(res.toString());
        } catch (BaseXException e) {
            logger.error("Error while running xquery script. " + e.getMessage());
        } catch (QueryException e) {
            logger.error("Error while running xquery script. " + e.getMessage());
        } catch (IOException e) {
            logger.error("Error while running xquery script. " + e.getMessage());
        } finally {
            context.close();
        }
    }
}
