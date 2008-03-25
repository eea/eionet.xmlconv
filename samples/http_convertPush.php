<?php

/*
*  PHP script for testing testing XMLCONV Conversion Service
*
*  Author Enriko Käsper, TietoEnator
*/ 

$server_url = "http://localhost:8080/xmlconv/api";
$method_path = "/convertPush";
$dir = realpath('.');

$convert_file="seed-general-report.xml";
$convert_id="168";

		$filepathname = $dir."/".$convert_file;

		$r = new HttpRequest($server_url.$method_path, HttpRequest::METH_POST);
		$r->addPostFields(array('convert_id' => $convert_id));
		if (substr($convert_file, -3, 3)=="zip")
			$r->addPostFile('convert_file', $filepathname, 'application/x-zip-compressed');
		else
			$r->addPostFile('convert_file', $filepathname, 'text/xml;charset=UTF-8');
		try {
			//Send the request to XMLCONV
		    $resp = $r->send();
			//Send the response to browser
			HttpResponse::setContentType($r->getResponseHeader("Content-type"));
			HttpResponse::setHeader("Content-disposition",$r->getResponseHeader("Content-disposition"));
			HttpResponse::setData($resp->getBody());
			HttpResponse::send();
			return;
		} catch (HttpException $ex) {
		    echo $ex;
		}

?>