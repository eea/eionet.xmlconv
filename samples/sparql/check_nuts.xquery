xquery version "1.0";
declare namespace xmlconv="http://converters.eionet.europa.eu";
declare namespace sparql="http://www.w3.org/2005/sparql-results#";

(:  declare SPARQL endpoint:)
declare variable $xmlconv:SPARQL_URL := "http://localhost:8890/sparql";

(: helper function for constructing sparql URL :)
declare function xmlconv:getSparqlEndPointUri($sparql as xs:string){
    let $sparql := fn:encode-for-uri($sparql)
    
    (: limit search with graph :)
    let $defaultGraph := "http://ec.europa.eu/eurostat/ramon/rdfdata/nuts2008.rdf"
    
    let $uriParams := concat("default-graph-uri=", $defaultGraph, "&amp;query=", $sparql , "&amp;format=application/sparql-results+xml")
    let $uri := concat($xmlconv:SPARQL_URL, "?", $uriParams)

    return
        $uri
}
;

(: get all triples for given code :)
declare function xmlconv:getNutsByCode($code as xs:string){
    let $sparql := 
        concat("select * where {?s <http://ec.europa.eu/eurostat/ramon/ontologies/geographic.rdf#code> '", $code, "'}") 
    
    let $sparqlUri := xmlconv:getSparqlEndPointUri($sparql)

    return
        fn:doc( $sparqlUri)
}
;

(: get all nuts for given country code :)
declare function xmlconv:getNutsByCC($countryCode as xs:string){
    let $sparql := 
        concat("select * where {?s <http://ec.europa.eu/eurostat/ramon/ontologies/geographic.rdf#code> ?code FILTER regex (?code , '^", $countryCode, "', 'i')}") 
    
    let $sparqlUri := xmlconv:getSparqlEndPointUri($sparql)

    return
        fn:doc( $sparqlUri)
}
;

declare function xmlconv:checkRegionsInXml($xmlUrl as xs:string){
    let $regionCodes := fn:distinct-values( fn:doc($xmlUrl)//region/@code)
       
    for $code in $regionCodes
        where fn:count(xmlconv:getNutsByCode($code)//sparql:binding)=0
        return
            $code
 }
 ;

xmlconv:getNutsByCC("UK")
 
(: 
xmlconv:checkRegionsInXml("FI_bird.xml")
xmlconv:checkRegionsInXml("http://cdr.eionet.europa.eu/fi/eu/habides/envts79og/FI_bird_rap_2008.xml")
xmlconv:getNutsByCode("BE353")
xxxxx
:)