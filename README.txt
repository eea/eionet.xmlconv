Documentation:

Deployment on Linux:
====================
Depending on your configuration, ant might install two jar-files that are not
supposed to be installed into WEB-INF/lib: jsp-api.jar and servlet-api.jar
If you don't see any webpages, remove those two.

How to install the package
==========================

The package is installed with 'ant'. First you set the installation location
in build.properties. As in:

prefix=/var/lib/tomcat5/webapps

ant install

NB!!! it still requires entering <Host> tag manually to server.xml
because it cannot be done programmatically !! The script tells, how.
