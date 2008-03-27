<?php

/*
*  PHP script for testing testing XMLCONV Conversion Service
*
*  Author Enriko Käsper, TietoEnator
*/ 

$server_url = "http://80.235.29.171:8080/xmlconv/api";
$method_path = "/convert";

$convert_url="https://svn.eionet.europa.eu/repositories/Reportnet/Dataflows/HabitatsDirectiveArticle17/xmlfiles/general-instancefile.xml";
$convert_id="26";

$r = new HttpRequest($server_url.$method_path, HttpRequest::METH_POST);
$r->addPostFields(array('url' => $convert_url));
$r->addPostFields(array('convert_id' => $convert_id));

try {
	//Send the request to XMLCONV
    $resp = $r->send();
	//Send the response to browser
	HttpResponse::setContentType($r->getResponseHeader("Content-type"));
	HttpResponse::setHeader("Content-disposition",$r->getResponseHeader("Content-disposition"));
	HttpResponse::setData($resp->getBody());
	HttpResponse::send(true);
} catch (HttpException $ex) {
    echo $ex;
}

?>