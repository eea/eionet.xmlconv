
<%@ page import="eionet.gdem.ssr.Names" %>

  <TABLE cellSpacing=0 cellPadding=0 width=621 border=0>
    <TBODY>
      <TR>
					<%/* if (err!= null) { %>
						<h2><FONT color="#FF0000"><%=err%></FONT></h2>
					<% }*/ %>

        <TD align=bottom width=20 background=images/bar_filled.jpg  height=25></TD>
        <TD width=600 background=images/bar_filled.jpg height=25>
          <TABLE height=8 cellSpacing=0 cellPadding=0    border=0>
            <TBODY>
              <TR>
                <TD vAlign=bottom align=middle width=92><A 
                  href="http://www.eionet.eu.int/"><SPAN 
                  class=barfont>EIONET</SPAN></A></TD>
                <TD vAlign=bottom width=28><IMG height=24 
                  src="images/bar_hole.jpg" width=28></TD>
                <TD valign="BOTTOM" align="middle">
            		<%
            			String oHName=request.getParameter("name");
            			if (oHName!=null) {%><A href="main"><%}%>
                		<span class="barfont">Stylesheet Repository</span>
                    	<%if (oHName!=null) {%></A><%}%>
        		</TD>
        		<%if (oHName!=null) {%>
                <TD vAlign=bottom width=28><IMG height=24 
                  src="images/bar_hole.jpg" width=28></TD>
            		<TD valign=BOTTOM nowrap="true" align="middle">
                		<SPAN class="barfont"><%=oHName%></SPAN>
            		</TD>
        		<%}%>
                <TD vAlign=bottom width=28><IMG height=25 
                  src="images/bar_dot.jpg" width=28></TD>
    		  </TR>
    		</TBODY>
		  </TABLE>
		</TD>
	  </TR>
	</TBODY>
  </TABLE>

