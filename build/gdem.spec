Summary: GDEM Services
Name: gdem
Version: 1.0
Release: 1
Source0: %{name}.tgz
License: GNU
Group: System Environment/Applications
BuildRoot: %{_topdir}/tmp_gdem_root
BuildRequires: ant
Prefix: /prj
BuildArch: noarch

%description
GDEM demo services
%prep
%setup -q
%build
 cd build
 ant
%install
if [ ! -d %{buildroot}%{prefix}/gdem ]; then
  mkdirhier %{buildroot}%{prefix}/gdem/public/WEB-INF/lib
	mkdir %{buildroot}%{prefix}/gdem/public/WEB-INF/classes
	mkdir %{buildroot}%{prefix}/gdem/xsl
fi;


cp GDEMService.xml %{buildroot}%{prefix}/gdem
cp public/WEB-INF/lib/*.jar %{buildroot}%{prefix}/gdem/public/WEB-INF/lib
cp public/WEB-INF/classes/*.properties %{buildroot}%{prefix}/gdem/public/WEB-INF/classes
cp public/WEB-INF/web.xml %{buildroot}%{prefix}/gdem/public/WEB-INF
cp xsl/*.xsl %{buildroot}%{prefix}/gdem/xsl

%clean
#rm -r $RPM_BUILD_ROOT
cd ..
rm -r gdem-%{version}
%files
%{prefix}/gdem/GDEMService.xml
%{prefix}/gdem/public/WEB-INF/lib/gdem.jar
%{prefix}/gdem/public/WEB-INF/lib/avalon.jar
%{prefix}/gdem/public/WEB-INF/lib/batik.jar
%{prefix}/gdem/public/WEB-INF/lib/castor.jar
%{prefix}/gdem/public/WEB-INF/lib/fop.jar
%{prefix}/gdem/public/WEB-INF/lib/log4j.jar
%{prefix}/gdem/public/WEB-INF/lib/saxon7.jar
%{prefix}/gdem/public/WEB-INF/lib/uit-definition.jar
%{prefix}/gdem/public/WEB-INF/lib/uit-server.jar
%{prefix}/gdem/public/WEB-INF/lib/xerces.jar
%{prefix}/gdem/public/WEB-INF/lib/xmlrpc.jar
%{prefix}/gdem/xsl/simpletablehtml.xsl
%{prefix}/gdem/xsl/averagephhtml.xsl
%{prefix}/gdem/xsl/eper2html.xsl
%{prefix}/gdem/xsl/eper2pdf.xsl
%config 
%{prefix}/gdem/public/WEB-INF/classes/gdem.properties
%{prefix}/gdem/public/WEB-INF/classes/uit.properties
%{prefix}/gdem/public/WEB-INF/web.xml
