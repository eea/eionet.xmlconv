package eionet.gdem.services.impl;

import eionet.gdem.XMLConvException;
import eionet.gdem.qa.ListQueriesMethod;
import eionet.gdem.qa.XQueryService;
import eionet.gdem.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eionet.gdem.services.JobRequestHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JobRequestHandlerServiceImpl implements JobRequestHandlerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobRequestHandlerService.class);

    XQueryService xQueryService;

    @Autowired
    public JobRequestHandlerServiceImpl() {
        xQueryService = new XQueryService();
    }

    /**
     * This method is copied from XQueryService public Vector analyzeXMLFiles(Hashtable files) throws XMLConvException {
     *
     * @param filesAndSchemas - Structure with XMLschemas as a keys and values are list of XML Files
     * @return Hashtable result: Structure with JOB ids as a keys and source files as values
     * @throws XMLConvException If an error occurs.
     */
    @Override
    public HashMap analyzeMultipleXMLFiles(HashMap<String, List<String>> filesAndSchemas) throws XMLConvException {

        HashMap result = new HashMap();

        if (filesAndSchemas == null) {
            return result;
        }

        for (Map.Entry<String, List<String>> entry : filesAndSchemas.entrySet()) {
            String schema = entry.getKey();
            List<String> fileList = entry.getValue();
            if (Utils.isNullList(fileList)) {
                continue;
            }

            for (String file: fileList){
                // get all possible xqueries from db
                String newId = "-1"; // should not be returned with value -1;

                Vector queries = xQueryService.listQueries(schema);

                if (!Utils.isNullVector(queries)) {
                    for (int j = 0; j < queries.size(); j++) {

                        String query_id = String.valueOf( ( (Hashtable) queries.get(j)).get( ListQueriesMethod.KEY_QUERY_ID ));
                        newId = xQueryService.analyzeXMLFile( file, query_id , schema );
                        result.put(newId, file);
                    }
                }
            }
        }
        return result;
    }

}
