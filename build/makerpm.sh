####################################################################################
# makes RPM source package and install package
# of JAR
####################################################################################
# usage makerpm.sh [version]
# if version specified (1.0), the tag has to be created in CVS, called release_1_0
# if version is not specified,then 1.0, then the latestcode is taken from CVS
# and version no 1.0 is used as default

###################################################################################
# MAY NEED ADJUSTEMENT
# RPM directory : parent for SOURCE, BUILD and other rpm directories 
###################################################################################
rpm_dir=/usr/src/redhat
###################################################################################


ver=$1

if [ "$ver" = "" ] ; then
 ver="1.0"
 cvs export -D today -d gdem-$ver gdem
else
 rel=`echo $ver | tr "\." "_"`
 cvs export -r release_$rel -d gdem-$ver gdem
fi;


###########
# make tgz
###########
tar cfz gdem.tgz gdem-$ver/*.xml gdem-$ver/src gdem-$ver/public gdem-$ver/build gdem-$ver/xsl

###############################
# copy *.spec file to working
###############################
cp gdem-$ver/build/gdem.spec .

######################
# Remove temp folder
######################
rm -rf gdem-$ver

######################
# RPM source directory
######################
if [ -d $rpm_dir/SOURCES ]; then
	mv gdem.tgz $rpm_dir/SOURCES
else
	echo "RPM directory $rpm_dir does not exist. Cannot create RPM package"
	exit
fi;

##################
#build source rpm
##################
rpm -bs gdem.spec

echo "=====================";
echo "       Success!"
echo "=====================";

echo "====================================================";
echo "     TAR archive was created in $rpm_dir/SOURCES"
echo "    The source package was created in $rpm_dir/SRPMS "
echo "====================================================";

echo "Use rpm --rebuild [src package name] to build the binary package";


echo "======================================================";
echo " REMEMBER TO ADD GDEM <Host> tag to tomcat server.xml ";
echo " if not existing yet!! See example in the *.tgz "
echo " gdem/server.xml";
echo "======================================================";

# Remove gdem.spec from the working folder
rm gdem.spec
