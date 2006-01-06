Documentation:

XMLConv was part of the GDEM project. Therefore the user guide and installation
guide is located in the /gdem part of the source code repository.



How to install the GDEM package
================================

+ Get the build.xml file needed for all the actions
---------------------------------------------------
#svn export http://svn.eionet.eu.int/repositories/Reportnet/xmlconv/trunk/build.xml

+ Make a tgz or zip package
-----------------------------
#ant make_package

+ install from the zip as a new virtual host (EEA's way)
--------------------------------------------------------
#ant install -Dprefix=/prj -Dvhost=true

NB!!! it still requires entering <Host> tag manually to server.xml
because it cannot be done programmatically !! The script tells, how.


More examples
=============

+ install from the zip as an application under an exisitng host (tomcat)
------------------------------------------------------------------------
#ant install -Dprefix=/webapps

create a package from an earlier version (requires the tag in CVS)
-------------------------------------------------------------------
#ant make_package -Dcvs.version=release_1_0
