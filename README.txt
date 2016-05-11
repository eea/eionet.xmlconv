****************************************
  QA and Converters (XMLCONV) Software    
****************************************

Prerequisites:
 * Java 1.6 or higher
 * Maven 3.0 or higher
 * Tomcat 6
 * MySql 5.5

Quick Installation Guide:

1. Setup environment properties:
================================
Crete local.properties file by copying the contents from default.properties as an example.
Edit the values of system paths, DB url and other properties to what is relevant.

"app.home" property represents the system folder which is the root for subfolders where all content files will be stored.
Tomcat user should have write permissions on "app.home" subfolders.

If it is a first time install then acl folder should be copied manually into ${app.home}/acl

2. Building the war:
====================
2.1 Execute command: mvn install

2.2 Rename *.acl.dist files to *.acl files in %app.home%/acls folder and revise the contents of acladmin.group file

3. Deploy 
===========
If build goes well, Maven says BUILD SUCCESSFUL and you will have xmlconv.war file in "target" directory.
It's possible to deploy the WAR into your Tomcat's webapps directory (either manually or via Tomcat's web console like we do at our demo site).

4. BaseX XQuery Client/Server
===========
4.x You can setup a BaseX server that will be responsible to execute XQuery 3.0 scripts. To do that, you need to pull the basexhttp image from
https://hub.docker.com/r/basex/basexhttp/

Make sure to change to default username and password of the server.
You can configure the XMLCONV application to connect to the BaseX server through the "Configure" menu.

5. Rancher Based deployments:
5.1 To build the application for use with the rancher environment, you need to change the env variable in pom.xml from local to rancher.
  This will make the application use the configuration from rancher.properties. This is a temporary solution until the full dockerization of XMLCONV.


Differences with the ANT build version:
=======================================
The mysql JDBC driver is in the project's war file and must not be in the Tomcat's lib folder.

Application's datasource is defined inside war, in META-INF/context.xml file. So in Tomcat's server.xml there don't have to be datasource configuration.
