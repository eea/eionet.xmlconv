xquery version "1.0";
declare namespace xmlconv="http://converters.eionet.europa.eu";
declare namespace sparql="http://www.w3.org/2005/sparql-results#";

declare variable $xmlconv:SPARQL_URL := "http://localhost:8890/sparql";


declare function xmlconv:getSparqlEndPointUri($sparql as xs:string){
    let $sparql := fn:encode-for-uri($sparql)
    let $defaultGraph := "http://ec.europa.eu/eurostat/ramon/rdfdata/nuts2008.rdf"
    let $uriParams := concat("default-graph-uri=", $defaultGraph, "&amp;query=", $sparql , "&amp;format=application/sparql-results+xml")
    let $uri := concat($xmlconv:SPARQL_URL, "?", $uriParams)

    return
        $uri
}
;

declare function xmlconv:getNutsByCode($code as xs:string){
    let $sparql := 
        concat("select * where {?s <http://ec.europa.eu/eurostat/ramon/ontologies/geographic.rdf#code> '", $code, "'}") 
    
    let $sparqlUri := xmlconv:getSparqlEndPointUri($sparql)

    return
        fn:doc( $sparqlUri)
(:        "select * where {?s <http://ec.europa.eu/eurostat/ramon/ontologies/geographic.rdf#hasParentRegion> <http://ec.europa.eu/eurostat/ramon/rdfdata/nuts2008/BE>}":)
}
;
declare function xmlconv:getNutsByCC($countryCode as xs:string){
    let $sparql := 
        concat("select * where {?s <http://ec.europa.eu/eurostat/ramon/ontologies/geographic.rdf#code> ?o FILTER regex (?o , '^", $countryCode, "', 'i')}") 
    
    let $sparqlUri := xmlconv:getSparqlEndPointUri($sparql)

    return
        fn:doc( $sparqlUri)
(:        "select * where {?s <http://ec.europa.eu/eurostat/ramon/ontologies/geographic.rdf#hasParentRegion> <http://ec.europa.eu/eurostat/ramon/rdfdata/nuts2008/BE>}":)
}
;

declare function xmlconv:checkHabidesDelivery($xmlUrl as xs:string){
    let $nutsFI := fn:distinct-values( xmlconv:getHabidesNutsCodes($xmlUrl))
       
    for $code in $nutsFI
    (:let $code := concat($code , "f"):)
    where fn:count(xmlconv:getNutsByCode($code)//sparql:binding)=0
    return
      $code
 }
 ;
declare function xmlconv:getHabidesNutsCodes($xmlUrl as xs:string){
    for $node in fn:doc($xmlUrl)//region
       
    return
        fn:data($node/@code)
 }
 ;
xmlconv:checkHabidesDelivery("http://cdr.eionet.europa.eu/fi/eu/habides/envts79og/FI_bird_rap_2008.xml")
 
(: 
xmlconv:getNutsByCode("BE353")
xmlconv:getNutsByCC("UK")
xmlconv:checkHabidesDelivery("http://cdr.eionet.europa.eu/fi/eu/habides/envts79og/FI_bird_rap_2008.xml")

:)