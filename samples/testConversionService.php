<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<?php
/*
*	PHP script for testing XMLCONV Conversion Service methods.
*
*	Script is tested on PHP 5.0 + http extension 
*
*	The script requires seed-general-report.xml and seed-general-report.zip 
*	files stored in the same directory	as php file.
*
*	Author Enriko Käsper, TietoEnator
*/ 

	
//global parameters
	$server_url = "http://localhost:8080/xmlconv/api";
	$dir = realpath('.');


//read SESSION parameters
	session_start();
	$schemas=$_SESSION['schemas'];
	$conversions=$_SESSION['conversions'];
	
	$convert_url=$_SESSION['convert_url'];
	$convert_file=$_SESSION['convert_file'];
	$convert_id=$_SESSION['convert_id'];
	$schema=$_SESSION['schema'];
	$xmlfiles=$_SESSION['xmlfiles'];
	if (count($files==0))loadXMLFiles();


//read request parameters and store these in the session
	$action=$_GET['action'];

	if($action=="convertPush"){
		$convert_file=$_GET['convert_file'];
		$convert_id=$_GET['convert_id'];
		convertPush( $convert_file, $convert_id);
		$_SESSION['convert_file']=$convert_file;
		$_SESSION['convert_id']=$convert_id;
		return;
	}
	elseif($action=="convert"){
		$convert_url=$_GET['convert_url'];
		$convert_id=$_GET['convert_id'];

		convert( $convert_url, $convert_id);
		
		$_SESSION['convert_url']=$convert_url;
		$_SESSION['convert_id']=$convert_id;
		$_SESSION['conversions']=$conversions;
		return;
	}
	elseif($action=="getXMLSchemas"){
		getXMLSchemas();
		$_SESSION['schemas']=$schemas;
	}
	elseif($action=="listConversions"){
		$schema=$_GET['schema'];
		listConversions($schema);
		$_SESSION['schema']=$schema;
	}
	elseif($action=="clear"){
		session_destroy(); 
	}

/**
*	Function calls convrtPush method
*/
	function convertPush($convert_file,$convert_id){
		global $server_url, $dir;
		$method_path = "/convertPush";
		//NB the source file has to be in the same directory or modify the path info in $dir variable

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
	}
/**
*	Function calls convrt method
*/
	function convert($convert_url,$convert_id){
		global $server_url;
		$method_path = "/convert";

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
			HttpResponse::send();
		} catch (HttpException $ex) {
		    echo $ex;
		}
	}
	/**
	*	Function calls getXMLSchemas method
	*/
	function getXMLSchemas(){
		global $server_url, $schemas;
		$method_path = "/getXMLSchemas";

		reset($schemas);
		$xml=simplexml_load_file($server_url.$method_path);
		$i=0;
		foreach($xml->schema as $item){
			$schemas[$i]="$item";
			$i++;
		}

	}
	/**
	*	Function calls listConversions method
	*/
	function listConversions($schema){
		global $server_url, $conversions;
		$method_path = "/listConversions";

		$xml=simplexml_load_file($server_url.$method_path."?schema=".$schema);
		$conversions = $xml->conversion;

	}
	/**
	*	load xml and zip files from the related directory
	*/
	function loadXMLFiles($schema){
		global $dir, $xmlfiles;

		$d = dir($dir);
		$i = 0;
		reset($xmlfiles);
		while (false !== ($entry = $d->read())) {
			if(substr($entry,-3,3)=="zip" || substr($entry,-3,3)=="xml"){
				$xmlfiles[$i] = $entry;
				$i++;
			}
		}
		$d->close();
		$_SESSION['xmlfiles']=$xmlfiles;
	}
?>
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
		<h1>Test XMLCONV conversion methods - PHP</h1>
		<form id="listConversions" method="get" action="testConversionService.php">
			<fieldset>
				<legend>1. "listConversions" method - search for available conversions</legend>
				<div class="row" style="display:none">
					<label for="server">Server URL:</label><input type="text" id="server" name="server" value="<?php echo($server_url); ?>"/>
				</div>
				<div class="row">
					<label for="selectSchema">Select XML Schema:</label>
					<select id="selectSchema" name="schema">
						<?php
						foreach($schemas as $item){
							if($item==$schema)
								print("<option selected=\"selected\" value=\"".$item."\">".$item."</option>");
							else
								print("<option value=\"".$item."\">".$item."</option>");
						}
						?>
					</select>
					<input type="submit" value="getXMLSchemas" name="action"/>
					<input type="button" onclick="getXMLSchemasXML()" value="show getXMLSchemas XML"/>
				</div>
				<div class="row">
					<label for="selectConversion">Select conversion:</label>
					<select id="selectConversion" name="conversionId" onchange="setConversionId()">
						<?php
						foreach($conversions as $item){
							print("<option value=\"".$item->convert_id."\">".$item->result_type." - ".$item->description."</option>");
						}
						?>
					</select>
					<input type="submit" value="listConversions" name="action"/>
					<input type="button" onclick="listConversionsXML()" value="show listConversions XML"/>
				</div>
			</fieldset>
		</form>

		<form id="convert" method="get" action="testConversionService.php">
			<fieldset>
				<legend>2. "convert" method - insert the URL of XML file and execute the conversion</legend>
				<label for="convert_url">URL of XML file:</label><input type="text" id="convert_url" name="convert_url" value="<?php echo($convert_url); ?>"/>
				<br/>
				<label for="convert_id1">Conversion ID:</label><input type="text" id="convert_id1" name="convert_id" value="<?php echo($convert_id); ?>"/>
				<br/>
				<input type="submit" value="convert" name="action"/>
			</fieldset>
		</form>

		<form id="convertPush" method="get" action="testConversionService.php">
			<fieldset>
				<legend>3. "convertPush" method - select the XML (or zipped XML) file name stored in the server and execute the conversion</legend>
				<label>Select XML file:</label><br/>
				<div class="radioContainer">
				<?php
				foreach ($xmlfiles as $item){
					$checked =  ($item==$convert_file) ? "checked=\"checked\"" : "";
					print("<input type=\"radio\" name=\"convert_file\" id=\"".$item."\" value=\"".$item."\" ".$checked."/><label class=\"radioLabel\" for=\"".$item."\">".$item."</label><br/>");
				}
				?>
				</div>
				<br/>
				<label for="convert_id2">Conversion ID:</label><input type="text" id="convert_id2" name="convert_id" value="<?php echo($convert_id); ?>"/>
				<br/>
				<input type="submit" value="convertPush" name="action"/>
			</fieldset>
		</form>

		<form id="clear" method="get" action="testConversionService.php">
			<fieldset>
				<input type="submit" value="clear" name="action"/>
			</fieldset>
		</form>
</body>
</html>
