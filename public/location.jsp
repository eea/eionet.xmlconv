
<%@ page import="eionet.gdem.conversion.ssr.Names" %>

  <table cellSpacing="0" cellPadding="0" border="0" width="100%">
      <tr>
 	    <td width="20" background="images/bar_filled.jpg"  height="25">&nbsp;</td>
        <td background="images/bar_filled.jpg" height="25">
          <table background="" border="0" cellpadding="0" cellspacing="0" height="10">
              <tr>
                <td valign="bottom" align="left"><span class="barfont"><a href="http://www.eionet.eu.int/">EIONET</a></span></td>
                <td valign="bottom" align="left"><img height="24" src="images/bar_hole.jpg" width="28"></td>
                <td valign="bottom" align="left">
            		<%
            			String oHName=request.getParameter("name");
            			if (oHName!=null) {%><span class="barfont"><a href="main"><%}%>
                			Stylesheet Repository
                    	<%if (oHName!=null) {%></a></span><%}%>
        		</td>
        		
        		<%if (oHName!=null) {%>
	                <td valign="bottom" align="left"><img height="24" src="images/bar_hole.jpg" width="28"></td>
	                <td valign="bottom" align="left" nowrap="true">
                		<span class="barfont"><%=oHName%></span>
            		</td>
        		<%}%>
                <td valign="bottom" align="left"><img height="25" src="images/bar_dot.jpg" width="28"></td>
    		  </tr>
		  </table>
		</td>
	  </tr>
  </table>

