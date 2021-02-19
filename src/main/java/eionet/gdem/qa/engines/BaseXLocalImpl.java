package eionet.gdem.qa.engines;

import eionet.gdem.Properties;
import eionet.gdem.XMLConvException;
import eionet.gdem.api.errors.BaseXJobInterruptedException;
import eionet.gdem.http.FollowRedirectException;
import eionet.gdem.http.HttpFileManager;
import eionet.gdem.qa.XQScript;
import eionet.gdem.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.basex.core.Context;
import org.basex.core.MainOptions;
import org.basex.core.cmd.Set;
import org.basex.io.out.ArrayOutput;
import org.basex.io.serial.SerializerOptions;
import org.basex.query.QueryException;
import org.basex.query.QueryProcessor;
import org.basex.query.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;

import static java.util.Objects.isNull;

/**
 * @author George Sofianos
 *
 */
public class BaseXLocalImpl extends QAScriptEngineStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseXLocalImpl.class);

    @Override
    protected void runQuery(XQScript script, OutputStream result) throws XMLConvException {

        Context context = new Context();
        QueryProcessor proc = null;
        try {
            new Set(MainOptions.INTPARSE, true).execute(context);
            new Set(MainOptions.MAINMEM, true).execute(context);

            String scriptSource = null;
            if (!Utils.isNullStr(script.getScriptSource())) {
                scriptSource = script.getScriptSource();
            } else if (!Utils.isNullStr(script.getScriptFileName())) {
                try (Reader queryReader = new FileReader(script.getScriptFileName())) {
                    scriptSource = new String(IOUtils.toByteArray(queryReader, "UTF-8"));
                } catch (IOException e) {
                    LOGGER.error("Error while reading XQuery file: " + e);
                    throw new XMLConvException("Error while reading XQuery file: " + script.getScriptFileName() + " : " + ExceptionUtils.getStackTrace(e), e);
                }
            }
            proc = new QueryProcessor(scriptSource, Properties.queriesFolder + "/script", context);

            HttpFileManager fileManager = new HttpFileManager();
            URL url = fileManager.followUrlRedirectIfNeeded(new URL(script.getSrcFileUrl()), null);
            script.setSrcFileUrl(url.toString());
            LOGGER.info("Script Source URL:"+script.getSrcFileUrl());
            proc.bind("source_url", script.getSrcFileUrl(), "xs:string");

//            proc.bind("base_url", Properties.gdemURL + Properties.contextPath , "xs:string");

            // same serialization options with saxon
            SerializerOptions opts = new SerializerOptions();

            opts.set(SerializerOptions.INDENT, "no");
            opts.set(SerializerOptions.ENCODING, DEFAULT_ENCODING);
            if (getOutputType().equals(HTML_CONTENT_TYPE)) {
                opts.set(SerializerOptions.METHOD, HTML_CONTENT_TYPE);
            } else {
                opts.set(SerializerOptions.METHOD, getOutputType());
            }

            if (getOutputType().equals(XML_CONTENT_TYPE)) {
                opts.set(SerializerOptions.OMIT_XML_DECLARATION, "no");
            } else {
                opts.set(SerializerOptions.OMIT_XML_DECLARATION, "yes");
            }

            Value res = proc.value();

            ArrayOutput A = res.serialize(opts);
            result.write(A.toArray());

            //logger.info("proc info: " + proc.info());
            //logger.info( new String(A.buffer() , "UTF-8" ));
        } catch (QueryException | IOException | FollowRedirectException e) {
            if (Thread.currentThread().isInterrupted()) {
                throw new XMLConvException(e.getMessage());
            } else {
                LOGGER.error("Error executing BaseX xquery script : " + e.getMessage());
                throw new XMLConvException(e.getMessage(),e.getCause());
            }
        } finally {
            if (!isNull(proc))  {
                proc.close();
            }
            context.close();
        }
    }
}
