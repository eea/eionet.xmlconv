
function Click(Target) {
 if (Net != 1){
  if (Target != gTarget) {
   document[Target].src = over.src;
   document[gTarget].src = out.src;
   gTarget = Target;
   gammel.src = document[Target].src;
  }
 }
}

function Over(Target) {
 if (Net != 1){
  gammel.src = document[Target].src;
  document[Target].src = over.src;
 }
}

function Out(Target) {
 if (Net != 1){
  document[Target].src = gammel.src;
 }
}


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
		window.open("login.jsp","login","height=230,width=400,status=no,toolbar=no,scrollbars=no,resizable=no,menubar=no,location=no");
	}
	function detectBrowser(){
		Net=1;

		if ((navigator.appName.substring(0,5) == "Netsc"
			&& navigator.appVersion.charAt(0) > 2)
			|| (navigator.appName.substring(0,5) == "Micro"
			&& navigator.appVersion.charAt(0) > 3)) {
		 Net=0;

		 over = new Image;
		 out = new Image;
		 gammel = new Image;

		 over.src = "images/on.gif";
		 out.src = "images/off.gif";
		 
		 gTarget = 'img1';
		}
	}

	function openPage(action) {
		document.forms["f"].ACTION.value=action;
		document.forms["f"].submit();
	}
