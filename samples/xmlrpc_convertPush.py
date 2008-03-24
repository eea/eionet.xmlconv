#   Script for testing XMLCONV Conversion Service
#   convertPush
#
#   Author: Enriko Kasper

import xmlrpclib

#server_url="http://converters.eionet.eu.int/RpcRouter"
server_url="http://localhost:8080/xmlconv/RpcRouter"
server = xmlrpclib.Server(server_url)
try:
	#open the file XML file and read it into byte array
	infilename = "C:/Projects/xmlconv/public/tmp/general-report.xml"
	fin = open(infilename, 'rb')
	contents = fin.read()
	fin.close()

	#file parameter value encoded as Base64 byte array
	param1  = xmlrpclib.Binary(contents)
	#convert_id parameter
	param2="168"
	#filename paramter
	param3="ResultFile.html"
	method_result=server.ConversionService.convertPush(param1,param2,param3)

except xmlrpclib.ProtocolError, p:
         err_code=p.errcode    #handle error according to error code

print method_result
