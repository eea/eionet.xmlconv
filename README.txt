****************************************
  QA and Converters (XMLCONV) Software    
****************************************

Prerequisites:
 * Java 1.6
 * Maven 2.0.4 or higher
 * Tomcat 5.5 or higher
 * MySql 5.5 or higher


Quick Installation Guide:

1. Setup environment properties:
================================
Crete local.properties file by copying the contents from default.properties as an example.
Edit the values of system paths, DB url and other proeprties to what is relevant.

"app.home" property represents the system folder which is the root for subfolders where all content files will be stored.

If it is a first time install then acl folder should be copied manually into ${app.home}/acl

2. Building the war:
====================
Execute command: mvn install

3. Deploy 
===========
If build goes well, Maven says BUILD SUCCESSFUL and you will have xmlconv.war file in "target" directory.
Bow it's possible to deploy the WAR into your Tomcat's webapps directory (either manually or via Tomcat's web console like we do at our demo site). 



Differences with the ANT build version:
=======================================
The mysql JDBC driver is in the project's war file and must not be in the Tomcat's lib folder.

Application's datasource is defined inside war, in META-INF/context.xml file. So in Tomcat's server.xml there don't have to be datasource configuration.
