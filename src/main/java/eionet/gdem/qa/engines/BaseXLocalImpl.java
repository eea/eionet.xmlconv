package eionet.gdem.qa.engines;

import eionet.gdem.XMLConvException;
import eionet.gdem.qa.XQScript;
import eionet.gdem.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.basex.core.Context;
import org.basex.core.MainOptions;
import org.basex.core.cmd.Set;
import org.basex.io.out.ArrayOutput;
import org.basex.query.QueryException;
import org.basex.query.QueryProcessor;
import org.basex.query.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;

import static java.util.Objects.isNull;

/**
 * @author George Sofianos
 *
 */
public class BaseXLocalImpl extends QAScriptEngineStrategy {

    private static final Logger logger = LoggerFactory.getLogger(BaseXLocalImpl.class);

    @Override
    protected void runQuery(XQScript script, OutputStream result) throws XMLConvException {

        Context context = new Context();
        QueryProcessor proc = null;
        try {
            new Set(MainOptions.INTPARSE, true).execute(context);

            String scriptSource = null;
            if (!Utils.isNullStr(script.getScriptSource())) {
                scriptSource = script.getScriptSource();
            } else if (!Utils.isNullStr(script.getScriptFileName())) {
                try (Reader queryReader = new FileReader(script.getScriptFileName())) {
                    scriptSource = new String(IOUtils.toByteArray(queryReader, "UTF-8"));
                } catch (IOException e) {
                    logger.error("Error while reading XQuery file: " + e);
                    throw new XMLConvException("Error while reading XQuery file: " + script.getScriptFileName() + " : " + ExceptionUtils.getStackTrace(e), e);
                }
            }
            proc = new QueryProcessor( scriptSource, context);
            proc.bind("source_url", script.getOrigFileUrl(), "xs:string");

            Value res = proc.value();

            ArrayOutput A = res.serialize();
            result.write(A.buffer());

            logger.debug("proc info: " + proc.info());
        } catch ( QueryException | IOException e) {
            logger.error("Error executing BaseX xquery script : " + e.getMessage());
            throw new XMLConvException(e.getMessage());
        } finally {
            if (!isNull(proc))  {
                proc.close();
            }
            context.close();
        }
    }
}
