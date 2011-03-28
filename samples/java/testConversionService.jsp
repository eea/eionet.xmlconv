<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="java.util.*"%>
<%@page import="javax.xml.parsers.DocumentBuilderFactory, javax.xml.parsers.DocumentBuilder, org.xml.sax.SAXException, org.xml.sax.SAXParseException"%>
<%@page import="java.io.File, org.w3c.dom.Document, org.w3c.dom.*"%>
<%@page import="java.net.URL, java.io.OutputStream, java.io.InputStream, java.io.IOException,org.apache.commons.httpclient.methods.multipart.*"%>
<%@page import="org.apache.commons.httpclient.*,org.apache.commons.httpclient.methods.*,org.apache.commons.httpclient.params.HttpMethodParams,org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity,org.apache.commons.httpclient.methods.multipart.FilePart;"%>
<%! final static String server_url = "http://80.235.29.171:8080/xmlconv/api";%>

<%
/*
*	JSP page for testing XMLCONV Conversion Service methods.
*
*	Script is tested on Java 5.0 and requires http-client-3.1 and commons-codec-2.1 libraries
*
*	The script requires seed-general-report.xml and seed-general-report.zip
*	files stored in the same directory	as php file.
*
*	Author Enriko Käsper, TietoEnator
*/



//read SESSION parameters
    //HttpSesssion session = request.getSession(true);
    List<String> schemas= (List<String>)session.getAttribute("schemas");
    List<Map<String,String>> conversions=(List)session.getAttribute("conversions");
    String convert_url=(String)session.getAttribute("convert_url");
    String convert_file=(String)session.getAttribute("convert_file");
    String convert_id=(String)session.getAttribute("convert_id");
    String schema=(String)session.getAttribute("schema");
    List<String> xmlfiles=(List)session.getAttribute("xmlfiles");

    if (xmlfiles==null){
        xmlfiles = loadXMLFiles();
        session.setAttribute("xmlfiles",xmlfiles);
    }


//read request parameters and store these in the session
    String action=request.getParameter("action");
    if(action==null) action="";

    if(action.equals("convertPush")){
        convert_file=request.getParameter("convert_file");
        convert_id=request.getParameter("convert_id");

        convertPush( response, convert_file, convert_id);

        session.setAttribute("convert_file",convert_file);
        session.setAttribute("convert_id",convert_id);
        return;
    }
    else if(action.equals("convert")){
        convert_url=request.getParameter("convert_url");
        convert_id=request.getParameter("convert_id");

        convert(response, convert_url, convert_id);

        session.setAttribute("convert_url",convert_url);
        session.setAttribute("convert_id",convert_id);

        return;
    }
    else if(action.equals("getXMLSchemas")){
        schemas = getXMLSchemas();
        session.setAttribute("schemas",schemas);
    }
    else if(action.equals("listConversions")){
        schema=request.getParameter("schema");
        conversions = listConversions(schema);

        session.setAttribute("conversions",conversions);
        session.setAttribute("schema",schema);
    }
    else if(action.equals("clear")){
        session.invalidate();
    }
%>
<%!
/**
*	Function calls convrtPush method
*/
    public void convertPush(HttpServletResponse response, String convert_file, String convert_id){
        String method_path = "/convertPush";
        //NB the source file has to be in the same directory or modify the path info in $dir variable

        String dir = getServletConfig().getServletContext().getRealPath("");
        String filepathname = dir.concat("/").concat(convert_file==null ? "":convert_file);
        File targetFile = new File(filepathname);
        // Create an instance of HttpClient.
        HttpClient client = new HttpClient();

        // Create a method instance.
        PostMethod filePost = new PostMethod(server_url.concat(method_path));
        try{
            Part[] parts = {
                    new FilePart("convert_file", targetFile),
                    new StringPart("convert_id",convert_id)
                };
            filePost.setRequestEntity(
                    new MultipartRequestEntity(parts, filePost.getParams())
                    );
            // execute method and handle any error responses.
            int statusCode = client.executeMethod(filePost);

            // handle response.
            if (statusCode != HttpStatus.SC_OK) {
              System.err.println("Method failed: " + filePost.getStatusLine());
            }

            // Read the response body.
            byte[] responseBody = filePost.getResponseBody();

            // Deal with the response.
            response.setContentType(filePost.getResponseHeader("Content-type").getValue());
            response.setHeader("Content-disposition",filePost.getResponseHeader("Content-disposition").getValue());
            response.setContentLength(responseBody.length);
            OutputStream out = response.getOutputStream();
            out.write(responseBody);
             out.close();
             out.flush();



            } catch (HttpException e) {
              System.err.println("Fatal protocol violation: " + e.getMessage());
              e.printStackTrace();
            } catch (IOException e) {
              System.err.println("Fatal transport error: " + e.getMessage());
              e.printStackTrace();
            } finally {
              // Release the connection.
              filePost.releaseConnection();
            }

        }
/**
*	Function calls convert method
*/
    public void convert(HttpServletResponse response, String convert_url, String convert_id){
        String method_path = "/convert";

        // Create an instance of HttpClient.
        HttpClient client = new HttpClient();

        // Create a method instance.
        PostMethod post = new PostMethod(server_url.concat(method_path));
        try{
            NameValuePair[] data = {
              new NameValuePair("url", convert_url),
              new NameValuePair("convert_id", convert_id)
            };
            post.setRequestBody(data);
            // execute method and handle any error responses.
            int statusCode = client.executeMethod(post);

            // handle response.
            if (statusCode != HttpStatus.SC_OK) {
              System.err.println("Method failed: " + post.getStatusLine());
            }

            // Read the response body.
            byte[] responseBody = post.getResponseBody();

            // Deal with the response.
            response.setContentType(post.getResponseHeader("Content-type").getValue());
            response.setHeader("Content-disposition",post.getResponseHeader("Content-disposition").getValue());
            response.setContentLength(responseBody.length);
            OutputStream out = response.getOutputStream();
            out.write(responseBody);
             out.close();
             out.flush();



            } catch (HttpException e) {
              System.err.println("Fatal protocol violation: " + e.getMessage());
              e.printStackTrace();
            } catch (IOException e) {
              System.err.println("Fatal transport error: " + e.getMessage());
              e.printStackTrace();
            } finally {
              // Release the connection.
              post.releaseConnection();
            }
    }
    /**
    *	Function calls getXMLSchemas method
    */
    public List<String> getXMLSchemas(){
        String method_path = "/getXMLSchemas";
        List<String> schemas = new ArrayList<String>();

        try{

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

            URL url = new URL(server_url.concat(method_path));
            InputStream stream = url.openStream();
            Document doc = docBuilder.parse(stream);

            // normalize text representation
            doc.getDocumentElement ().normalize ();
            NodeList nodeList = doc.getElementsByTagName("schema");
            for (int s = 0; s < nodeList.getLength(); s++) {
                Element elem = (Element)nodeList.item(s);
                String xmlSchema = elem.getFirstChild().getNodeValue();
                schemas.add(xmlSchema);
            }
        }catch (SAXParseException err) {
            System.out.println ("** Parsing error" + ", line "
                 + err.getLineNumber () + ", uri " + err.getSystemId ());
            System.out.println(" " + err.getMessage ());
        }catch (SAXException e) {
            Exception x = e.getException ();
            ((x == null) ? e : x).printStackTrace ();
        }catch (Throwable t) {
            t.printStackTrace ();
        }
        return schemas;
    }
    /**
    *	Function calls listConversions method
    */
    public List<Map<String,String>> listConversions(String schema){
        String method_path = "/listConversions";
        List<Map<String,String>> conversions = new ArrayList<Map<String,String>>();

        try{

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

            URL url = new URL(server_url.concat(method_path).concat("?schema=").concat(schema));
            InputStream stream = url.openStream();
            Document doc = docBuilder.parse(stream);

            // normalize text representation
            doc.getDocumentElement ().normalize ();
            NodeList nodeList = doc.getElementsByTagName("conversion");
            for (int s = 0; s < nodeList.getLength(); s++) {
                Element elem = (Element)nodeList.item(s);
                Node elemConvertIt = elem.getElementsByTagName("convert_id").item(0).getFirstChild();
                String convert_id = (elemConvertIt==null) ? "":elemConvertIt.getNodeValue();
                Node elemResultType = elem.getElementsByTagName("result_type").item(0).getFirstChild();
                String result_type = (elemResultType==null) ? "":elemResultType.getNodeValue();
                Node elemDescription = elem.getElementsByTagName("description").item(0).getFirstChild();
                String description =(elemDescription==null) ? "":elemDescription.getNodeValue();
                Map<String,String> conversion = new HashMap<String,String>();
                conversion.put("convert_id",convert_id);
                conversion.put("result_type",result_type);
                conversion.put("description",description);
                conversions.add(conversion);
            }
        }catch (SAXParseException err) {
            System.out.println ("** Parsing error" + ", line "
                 + err.getLineNumber () + ", uri " + err.getSystemId ());
            System.out.println(" " + err.getMessage ());
        }catch (SAXException e) {
            Exception x = e.getException ();
            ((x == null) ? e : x).printStackTrace ();
        }catch (Throwable t) {
            t.printStackTrace ();
        }
        return conversions;
    }
    /**
    *	load xml and zip files from the related directory
    */
    public List<String> loadXMLFiles(){
        List<String> xmlfiles = new ArrayList<String>();
        String dir = getServletConfig().getServletContext().getRealPath("");
        File directory = new File(dir);
        String files[]=directory.list();
        for(int i=0;i<files.length;i++){
            if(files[i].length()>3 &&
                    (files[i].substring(files[i].length()-3,files[i].length()).equals("zip") ||
                            files[i].substring(files[i].length()-3,files[i].length()).equals("xml"))){
                xmlfiles.add(files[i]);
            }
        }
        return xmlfiles;
    }
%>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>Test Conversion methods</title>
        <style type="text/css" media="screen">
        //<!--
            fieldset{
                margin: 2px;
            }
            legend{
                font-weight:bold;
                font-size: 12pt;
                color: black;
            }
            .row{
                display:block;
                width:100%;
            }
            label{
                width: 10em;
                background-color: #f0f0f0;
                vertical-align: top;
                margin-top: 5px;
                margin-right: 5px;
                float: left;
            }
            input[type="text"], input[type="file"]{
                width: 500px;
                margin-top: 5px;
            }
            input[type="radio"]{
                margin-top: 5px;
            }
            input[type="button"],input[type="submit"]{
                margin-top: 5px;
            }
            select{
                margin: 2px;
            }
            .radioLabel{
                width: 10em;
                background-color: white;
                vertical-align: top;
                margin: 3px;
                float: none;
            }
            .radioContainer{
                margin-top: 7px;
                margin-left: 30px;
                width:100%;
                display:block;
            }
        //-->
        </style>
        <script type="text/javascript">
        //<!--
        function getXMLSchemasXML(){
            document.location = document.getElementById("server").value +  "/getXMLSchemas";
        }
        function listConversionsXML(){
            document.location = document.getElementById("server").value +  "/listConversions?schema=" + document.getElementById("selectSchema").value;
        }
        function loadSchemas(){
            document.location = "testConversionService.php?action=getXMLSchemas";
        }
        function setConversionId(){
            var selectedConversion=document.getElementById("selectConversion").value;
            var inpObjects = document.getElementsByTagName("input");
            for (var i=0; i < inpObjects.length;i++){
                var name = inpObjects[i].getAttribute("name");
                if(name=="convert_id"){
                    inpObjects[i].value=selectedConversion;
                }
            }
        }
        function init(){
            setConversionId();
        }
        window.onload = init;
        //-->
        </script>
    </head>
    <body>
        <h1>Test XMLCONV conversion methods - JSP</h1>
        <form id="listConversions" method="get" action="testConversionService.jsp">
            <fieldset>
                <legend>1. "listConversions" method - search for available conversions</legend>
                <div class="row" style="display:none">
                    <label for="server">Server URL:</label><input type="text" id="server" name="server" value="<%=server_url%>"/>
                </div>
                <div class="row">
                    <label for="selectSchema">Select XML Schema:</label>
                    <select id="selectSchema" name="schema">
                        <%
                        for(int i=0; schemas!=null && i<schemas.size();i++){
                            String item = schemas.get(i);
                            String selected = (item!=null && item.equals(schema))? "selected=\"selected\"" : "";
                            %>
                            <option <%=selected%> value="<%=item%>"><%=item%></option>
                            <%
                        }
                        %>
                    </select>
                    <input type="submit" value="getXMLSchemas" name="action"/>
                    <input type="button" onclick="getXMLSchemasXML()" value="show getXMLSchemas XML"/>
                </div>
                <div class="row">
                    <label for="selectConversion">Select conversion:</label>
                    <select id="selectConversion" name="conversionId" onchange="setConversionId()">
                        <%
                        for(int i=0; conversions!=null && i<conversions.size();i++){
                            Map<String,String> item = conversions.get(i);
                            String convert_ID = (String)item.get("convert_id");
                            String result_type = (String)item.get("result_type");
                            String description = (String)item.get("description");
                            %>
                            <option value="<%=convert_ID%>"><%=result_type%> - <%=description%></option>
                            <%
                        }
                        %>
                    </select>
                    <input type="submit" value="listConversions" name="action"/>
                    <input type="button" onclick="listConversionsXML()" value="show listConversions XML"/>
                </div>
            </fieldset>
        </form>

        <form id="convert" method="get" action="testConversionService.jsp">
            <fieldset>
                <legend>2. "convert" method - insert the URL of XML file and execute the conversion</legend>
                <label for="convert_url">URL of XML file:</label><input type="text" id="convert_url" name="convert_url" value="<%=(convert_url==null)?"":convert_url%>"/>
                <br/>
                <label for="convert_id1">Conversion ID:</label><input type="text" id="convert_id1" name="convert_id" value="<%=convert_id%>"/>
                <br/>
                <input type="submit" value="convert" name="action"/>
            </fieldset>
        </form>

        <form id="convertPush" method="get" action="testConversionService.jsp">
            <fieldset>
                <legend>3. "convertPush" method - select the XML (or zipped XML) file name stored in the server and execute the conversion</legend>
                <label>Select XML file:</label><br/>
                <div class="radioContainer">
                <%
                for(int i=0;xmlfiles!=null && i<xmlfiles.size();i++){
                    String item = xmlfiles.get(i);
                    String checked =  item.equals(convert_file) ? "checked=\"checked\"" : "";
                    %>
                    <input type="radio" name="convert_file" id="<%=item%>" value="<%=item%>" <%=checked%>/><label class="radioLabel" for="<%=item%>"><%=item%></label><br/>
                    <%
                }
                %>
                </div>
                <br/>
                <label for="convert_id2">Conversion ID:</label><input type="text" id="convert_id2" name="convert_id" value="<%=convert_id%>"/>
                <br/>
                <input type="submit" value="convertPush" name="action"/>
            </fieldset>
        </form>

        <form id="clear" method="get" action="testConversionService.jsp">
            <fieldset>
                <input type="submit" value="clear" name="action"/>
            </fieldset>
        </form>
</body>
</html>
