### How to execute XMLRPC related scripts (named: xmlrpc_convertDD_XML*.py)

 - Install xmlrpc library <br>
    $ sudo pip3 install xmlrpc
 - Invoke the script using python 3 and providing 3 parameters(all inside double quotes):
   - xmlconv Server`RpcRouter` endpoint e.g "http://localhost:8080/RpcRouter"
   - excel file location , e.g:  "http://localhost:8081/Tables_Reporting_SE_2017_HP6-v1.xls"
   - Excel Sheet Name to be used in conversion, e.g: "NVZBoundaries" 
   
   Full Example: <br>
   
   python3 xmlrpc_convertDD_XML_split_removeEmpty_elems.py "http://localhost:8080/RpcRouter" "http://localhost:8081/Tables_Reporting_SE_2017_HP6-v1.xls" "NVZBoundaries" 


### how to serve excel files from local system to local xmlconv server for development purposes:
  One way is to install the http-server cmd program using npm. To do so execute in a shell: <br>
  $ npm install -g http-server
  - Then in order to serve the contents of a folder through http, open that folder in terminal and execute: <br>
    http-server 
   
### General note on executing all xmlrpc_*.py scripts
 - Examine each script manually and determine from the comments  what arguments to pass through command line