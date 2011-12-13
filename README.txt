Documentation:

Installation prerequisites:
===========================
Crete local.properties like default.properties as an example.

1. Copy the acl folder into ${app.home} folder
2. Copy the GDEMServices.xml into ${app.home} folder

(The ${app.home} is the property value in local.properties file)

Building the war:
=================
Execute command: mvn install

Differences with the ANT build version:
=======================================
The mysql JDBC driver is in the project's war file and must not be in the Tomcat's lib folder.

Application's datasource is defined inside war, in META-INF/context.xml file. So in Tomcat's server.xml there don't have to be datasource configuration.
