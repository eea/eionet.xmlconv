#   Script for testing XMLCONV XQueryService Service
#   runQAScript

import xmlrpc.client
import sys
server = xmlrpc.client.ServerProxy(sys.argv[1])

try:
	method_result = server.XQueryService.runQAScript("https://cdrtest.eionet.europa.eu/ro/colwkcutw/envxxyxia/REP_D-RO_ANPM_20170929_C-001.xml","1271")

except xmlrpc.client.ProtocolError as err:
	print("A protocol error occurred")
	print("URL: %s" % err.url)
	print("HTTP/HTTPS headers: %s" % err.headers)
	print("Error code: %d" % err.errcode)
	print("Error message: %s" % err.errmsg)

print(method_result)
