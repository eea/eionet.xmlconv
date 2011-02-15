xquery version "1.0";
declare namespace xmlconv="http://converters.eionet.europa.eu";
declare namespace sparql="http://www.w3.org/2005/sparql-results#";

declare variable $xmlconv:SPARQL_URL := "http://localhost:8890/sparql";


declare function xmlconv:getSparqlEndPointUri($sparql as xs:string){
    let $sparql := fn:encode-for-uri($sparql)

    let $defaultGraph := fn:encode-for-uri("http://converters.eionet.europa.eu/api/convert?convert_id=265&amp;url=http://converters.eionet.europa.eu/xmlfile/stations-min-max.xml")

    let $uriParams := concat("default-graph-uri=", $defaultGraph, "&amp;query=", $sparql , "&amp;format=application/sparql-results+xml")
    let $uri := concat($xmlconv:SPARQL_URL, "?", $uriParams)

    return
        $uri
}
;

declare function xmlconv:getCountryByLongLat($lat as xs:decimal, $long as xs:decimal){
    let $sparql := 
        concat("select ?s, ?country, ?minx, ?maxx, ?miny , ?maxy where {?s <http://rdfdata.eionet.europa.eu/eea/ontology/isoCode> ?country .
            ?s <http://rdfdata.eionet.europa.eu/eea/ontology/minx> ?minx . FILTER ( xsd:decimal(?minx)<=", $lat, ") 
            ?s <http://rdfdata.eionet.europa.eu/eea/ontology/maxx> ?maxx . FILTER ( xsd:decimal(?maxx)>=", $lat, ") 
            ?s <http://rdfdata.eionet.europa.eu/eea/ontology/miny> ?miny . FILTER ( xsd:decimal(?miny)<=", $long, ") 
            ?s <http://rdfdata.eionet.europa.eu/eea/ontology/maxy> ?maxy . FILTER ( xsd:decimal(?maxy)>=", $long, ") 

        }")
    
        let $sparqlUri := xmlconv:getSparqlEndPointUri(fn:normalize-space($sparql))

    return
       fn:doc($sparqlUri)
}
;

declare function xmlconv:getBoundingBoxes($lat as xs:decimal, $long as xs:decimal){
       
    for $result in xmlconv:getCountryByLongLat($lat, $long)//sparql:result
    return
        <tr>
            <td>{fn:data($result/sparql:binding[@name='country']/sparql:literal)}</td>
            <td>{fn:data($result/sparql:binding[@name='minx']/sparql:literal)}</td>
            <td>{fn:data($result/sparql:binding[@name='maxx']/sparql:literal)}</td>
            <td>{fn:data($result/sparql:binding[@name='miny']/sparql:literal)}</td>
            <td>{fn:data($result/sparql:binding[@name='maxy']/sparql:literal)}</td>
        </tr>
 }
 ;
declare function xmlconv:getCountry($lat as xs:decimal, $long as xs:decimal){
       
        <html>
            <body>
            <table border="1">
                <tr>
                    <th>country</th>
                    <th>minx</th>
                    <th>maxx</th>
                    <th>miny</th>
                    <th>maxy</th>
                </tr>
                {xmlconv:getBoundingBoxes($lat, $long)}
            </table>
            </body>
        </html>
 }
 ;
xmlconv:getCountry(12, 55)
 