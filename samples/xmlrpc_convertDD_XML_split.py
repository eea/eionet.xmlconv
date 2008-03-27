#   Script for testing XMLCONV Conversion Service
#   The script is converting Excel file to XML.
#
#   Author: Enriko Kasper


import xmlrpclib

#server_url="http://converters.eionet.eu.int/RpcRouter"
server_url="http://80.235.29.171:8080/xmlconv/RpcRouter"
server = xmlrpclib.Server(server_url)

try:
    #excel file
    param1="http://cdr.eionet.europa.eu/se/eea/ewn1/envr8broa/SE_Rivers_Revised_SoE2008.xls"
    #sheet name
    param2="Stations2"

    method_result=server.ConversionService.convertDD_XML_split(param1,param2)

except xmlrpclib.ProtocolError, p:
         err_code=p.errcode    #handle error according to error code

print method_result
