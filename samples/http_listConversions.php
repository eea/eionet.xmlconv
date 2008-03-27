<?php

/*
*  PHP script for testing testing XMLCONV Conversion Service
*
*  Author Enriko Käsper, TietoEnator
*/ 

$server_url = "http://80.235.29.171:8080/xmlconv/api";
$method_path = "/listConversions";
$schema_param = "http://biodiversity.eionet.europa.eu/schemas/dir9243eec/generalreport.xsd";

$xml=simplexml_load_file($server_url.$method_path."?schema=".$schema_param);

$response_code = $xml->attributes()->code;

if	($response_code==200){
	foreach($xml->conversion as $item){
		Print_r($item);
	}
}
else{
	echo "Error happened: ".$xml->error-message;
}

?>