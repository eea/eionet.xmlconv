/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is "EINRC-7 / GDEM project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2004 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Kaido Laine (TietoEnator)
 */

package eionet.gdem.qa;

import eionet.gdem.XMLConvException;
import eionet.gdem.dcm.remote.RemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * QA Service Service Facade. The service is able to execute different QA related methods that are called through XML/RPC and HTTP
 * POST and GET.
 *
 * @author Enriko Käsper
 */
@Service
public class QueryService extends RemoteService {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryService.class);


    /**
     * Default constructor.
     */
    @Autowired
    public QueryService() {
        // for remote clients use trusted mode
        setTrustedMode(true);
    }

    /**
     * List all possible XQueries for this namespace.
     * @param schema Schema
     * @throws XMLConvException If an error occurs.
     */
    public List<Hashtable> listQueries(String schema) throws XMLConvException {

        ListQueriesMethod method = new ListQueriesMethod();
        List<Hashtable> list = method.listQueries(schema);
        return list;
    }

    /**
     * List all XQueries and their modification times for this namespace returns also XML Schema validation.
     * @param schema Schema
     * @throws XMLConvException If an error occurs.
     */
    public Vector listQAScripts(String schema) throws XMLConvException {
        ListQueriesMethod method = new ListQueriesMethod();
        Vector v = method.listQAScripts(schema);
        return v;
    }

    /**
     * List all XQueries and their modification times for this namespace returns also XML Schema validation.
     * @param schema Schema
     * @param active filter by active status
     * @throws XMLConvException If an error occurs.
     */
    public Vector listQAScripts(String schema, String active) throws XMLConvException {
        ListQueriesMethod method = new ListQueriesMethod();
        Vector v = method.listQAScripts(schema, active);
        return v;
    }
}
