#   Script for testing XMLCONV Conversion Service
#   convertPush
#
#   Author: Enriko Kasper

import xmlrpclib

#server_url="http://converters.eionet.eu.int/RpcRouter"
#server_url="http://80.235.29.171:8080/xmlconv/RpcRouter"
server_url="http://192.168.0.45:8080/RpcRouter"
server = xmlrpclib.Server(server_url)
method_result=""
try:
	#open the file XML file and read it into byte array
	infilename = "/home/dev-gso/eea/Reportnet/Dataflows/MMR-PAMs/test/mmr_pams_v20150729.xml"
	fin = open(infilename, 'rb')
	contents = fin.read()
	fin.close()

	#file parameter value encoded as Base64 byte array
	param1  = xmlrpclib.Binary(contents)
	#convert_id parameter
	param2="437"
	#filename paramter
	param3="ResultFile.html"
	method_result=server.ConversionService.convertPush(param1,param2,param3)
except xmlrpclib.ProtocolError, p:
         err_code=p.errcode    #handle error according to error code

print method_result
