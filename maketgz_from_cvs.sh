####################################################################################
# makes a TGZ package, retrieves data from CVS
####################################################################################
# usage maketgz_from_cvs.sh [version]
# if version specified (1.0), the tag has to be created in CVS, called release_1_0
# if version is not specified,then 1.0, then the latestcode is taken from CVS
# and version no 1.0 is used as default


ver=$1

if [ "$ver" = "" ] ; then
 ver="1.0"
 cvs export -D today -d gdem gdem
else
 rel=`echo $ver | tr "\." "_"`
 cvs export -r release_$rel -d gdem gdem
fi;

###########
# make tgz
###########
tar cfz gdem.tar.gz gdem/*.xml gdem/src gdem/public gdem/build gdem/xsl

rm -fr gdem


echo "=====================";
echo "=       Success!    ="
echo "=====================";


