package eionet.gdem.qa.engines;

import org.apache.commons.io.IOUtils;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.MainOptions;
import org.basex.core.cmd.Set;
import org.basex.query.QueryException;
import org.basex.query.QueryProcessor;
import org.basex.query.iter.Iter;
import org.basex.query.value.Value;
import org.basex.query.value.item.Item;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author George Sofianos
 */
public class BasexImplTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void runQuery() throws Exception {
/*
        //Context context = new Context();
        Map xmls = new HashMap<Integer, String>();
        //xmls.put(1, "/home/dev-gso/demo-xquery/water-resources");
        //xmls.put(2, "/home/dev-gso/demo-xquery/water-abstraction.xml");
        xmls.put(3, "/home/dev-gso/demo-xquery/water-use-full.xml");
        Map xqs = new HashMap<Integer, String>();
        //xqs.put(1, "/home/dev-gso/demo-xquery/water-resources.xquery");
        //xqs.put(2, "/home/dev-gso/demo-xquery/water-abstraction.xquery");
        xqs.put(3, "/home/dev-gso/demo-xquery/water-use.xquery");

        int select = 3;
        String xmlPath = xmls.get(select).toString();
        String xqPath = xqs.get(select).toString();
        String script;
        FileInputStream inputStream = new FileInputStream(xqPath);
        try {
            script = IOUtils.toString(inputStream);
        } finally {
            inputStream.close();
        }
       // try {
       //     context.
        //final BaseXClient session = new BaseXClient("localhost", 1984, "admin", "admin");
        final InputStream xmlStream = new FileInputStream(xmlPath);
        Context context = new Context();
        try {
            // initialize timer
            final long time = System.nanoTime();

            //final BaseXClient.Query query = session.query(script);
           // session.create("testxx", xmlStream);
           // query.bind("source_url", xmlPath);
           // System.out.println(session.info());
           // System.out.println(query.execute());

            //System.out.println(session.execute("info index"));

            //System.out.println(session.execute("xquery doc('testxx')"));

            //query.close();

            // print time needed
            final double ms = (System.nanoTime() - time) / 1000000d;
            System.out.println("\n\n" + ms + " ms");

            System.setProperty("org.basex.DEBUG", "true");
            new Set(MainOptions.INTPARSE, true).execute(context);
            new Set(MainOptions.MAINMEM, true).execute(context);
            QueryProcessor proc = new QueryProcessor(script, context);
            proc.bind("source_url", xmlPath);
            System.out.println(proc.info());

            //Iter iter = proc.iter();

            // Iterate through all items and serialize
            //for(Item item; (item = iter.next()) != null;) {
            //    System.out.println(item.serialize());
           // }


            Value res = proc.value();
            System.out.println(res.serialize());

        } finally {
            context.close();
            //session.close();
        }
        */
    }
}