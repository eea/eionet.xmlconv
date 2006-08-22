
	function changeParamInString(sUrl, sName, sValue){
		var  i, j,  sBeg, sEnd, sStr;

		//KL 021009 -> in some reason does not work anymore :(
		//sValue=escape(sValue);

		i = sUrl.indexOf(sName + '=');
		if (i > 0) {
			sBeg=sUrl.substr(0, i);
			sStr=sUrl.substr(i);
			j = sStr.indexOf('&');
			if (j > 0)
			   sEnd = sStr.substr(j);
			else
			   sEnd= '';

			sUrl=sBeg + sName + '=' + sValue + sEnd ;

			}
		else
		{
			j = sUrl.indexOf('?');
			if (j>0)
				sUrl = sUrl + '&' + sName + '=' + sValue;
			else
				sUrl = sUrl + '?' + sName + '=' + sValue;
			}
		//return sUrl ;
		redirect(sUrl);
		}

	function redirect(url){
		//document.URL=url;
		document.location=url;
	}

	function logout() {
		document.forms["logout"].submit();
	}
	function login() {
		window.open("do/start?login=true","login","height=230,width=400,status=no,toolbar=no,scrollbars=no,resizable=no,menubar=no,location=no");
	}

	function openPage(action) {
		document.forms["f"].ACTION.value=action;
		document.forms["f"].submit();
	}
