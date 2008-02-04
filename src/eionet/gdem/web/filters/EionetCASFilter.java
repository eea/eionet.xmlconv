package eionet.gdem.web.filters;







import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.batik.script.Window.GetURLHandler;

import com.tee.uit.security.AppUser;

import edu.yale.its.tp.cas.client.filter.CASFilter;
import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;

public class EionetCASFilter extends CASFilter {


	
	public static final String EIONET_LOGIN_COOKIE_NAME = "eionetCasLogin";

	private static LoggerIF logger = GDEMServices.getLogger();

	private static final String EIONET_COOKIE_LOGIN_PATH = "eionetCookieLogin";

	private static String CAS_LOGIN_URL = null;

	private static String SERVER_NAME = null;

	private static String EIONET_LOGIN_COOKIE_DOMAIN = null;
	

	public void init(FilterConfig config) throws ServletException {
		CAS_LOGIN_URL = config.getInitParameter(LOGIN_INIT_PARAM);
		SERVER_NAME = config.getInitParameter(SERVERNAME_INIT_PARAM);
		EIONET_LOGIN_COOKIE_DOMAIN = config.getInitParameter("eionetLoginCookieDomain");
		super.init(config);

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc) throws ServletException, IOException {
		CASFilterChain chain = new CASFilterChain();
		super.doFilter(request, response, chain);

		if (chain.isDoNext()) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			HttpSession session = httpRequest.getSession();
			if (session != null && session.getAttribute("user") == null) {
				String username = (String) session.getAttribute(CAS_FILTER_USER);
				AppUser aclUser = new CASUser(username);
				httpRequest.getSession().setAttribute(Names.USER_ATT, aclUser);
				session.setAttribute("user", username);
				logger.debug("Logged in user " + username);
				String requestURI = httpRequest.getRequestURI();
				if (requestURI.indexOf(EIONET_COOKIE_LOGIN_PATH) > -1) {
					redirectAfterEionetCookieLogin(httpRequest, httpResponse);
					return;
				} else if (requestURI.endsWith("/login")) {
					attachEionetLoginCookie(httpResponse,true);
					if (session.getAttribute("afterLogin") != null)
						httpResponse.sendRedirect(session.getAttribute("afterLogin").toString());
					else
						request.getRequestDispatcher("/").forward(request,response);
					return;
				}
			}
			fc.doFilter(request, response);
			return;
		}
	}

	public static void attachEionetLoginCookie(HttpServletResponse response, boolean isLoggedIn){
		Cookie tgc = new Cookie(EIONET_LOGIN_COOKIE_NAME, isLoggedIn?"loggedIn":"loggedOut");
		tgc.setMaxAge(-1);
		if (!EIONET_LOGIN_COOKIE_DOMAIN.equalsIgnoreCase("localhost"))
			tgc.setDomain(EIONET_LOGIN_COOKIE_DOMAIN);
		tgc.setPath("/");			
		response.addCookie(tgc);		
	}
	
	
	public static String getCASLoginURL(HttpServletRequest request) {
		
		// Since Tomcat 5.5 getRequestURL() returns the JSP of the template (lauout.jsp).
		// If it is a Struts request, then it is a problem, because it is not the URL initiated by client browser.
		// use javax.servlet.forward.request_uri for getting the original URL
		//System.out.println(getReturnURL(request));
		request.getSession(true).setAttribute("afterLogin",getReturnURL(request));

		return CAS_LOGIN_URL + "?service=" + request.getScheme() + "://" + SERVER_NAME + request.getContextPath() + "/login";
	}

	public static String getCASLogoutURL(HttpServletRequest request) {
		return CAS_LOGIN_URL.replaceFirst("/login","/logout")+ "?url=" + request.getScheme() + "://" + SERVER_NAME + request.getContextPath();
	}
	
	
	public static String getEionetCookieCASLoginURL(HttpServletRequest request) {

		String contextPath = request.getContextPath();
		String serviceURL =  request.getRequestURL().toString(); 
		if (request.getQueryString() != null && request.getQueryString().length() > 0){
			serviceURL = serviceURL + "?" + request.getQueryString();
		}

		String serviceURI = serviceURL.substring(serviceURL.indexOf("/", serviceURL.indexOf("://") + 3));

		if (contextPath.equals("")) {
			if (serviceURI.equals("/"))
				serviceURL = serviceURL + EIONET_COOKIE_LOGIN_PATH + "/";
			else
				serviceURL = serviceURL.replaceFirst(forRegex(serviceURI), "/" + EIONET_COOKIE_LOGIN_PATH + serviceURI);
		} else {
			String servletPath = serviceURI.substring(contextPath.length(), serviceURI.length());
			if (serviceURI.equals("/"))
				serviceURL = serviceURL + EIONET_COOKIE_LOGIN_PATH + "/";
			else
				serviceURL = serviceURL.replaceFirst(forRegex(serviceURI), contextPath + "/" + EIONET_COOKIE_LOGIN_PATH + servletPath);
		}

		try {
			serviceURL = URLEncoder.encode(serviceURL,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		}
		
		return CAS_LOGIN_URL + "?service=" +   serviceURL ;

	}

	private void redirectAfterEionetCookieLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String requestUri = request.getRequestURI() + (request.getQueryString() != null ? ("?" +request.getQueryString()):"" );
		String realURI = null;
		if (requestUri.endsWith(EIONET_COOKIE_LOGIN_PATH + "/"))
			realURI = requestUri.replaceFirst(EIONET_COOKIE_LOGIN_PATH + "/", "");
		else
			realURI = requestUri.replaceFirst("/" + EIONET_COOKIE_LOGIN_PATH, "");
		
		response.sendRedirect(realURI);
		
	}

	  public static String forRegex(String aRegexFragment){
		    final StringBuffer result = new StringBuffer();

		    final StringCharacterIterator iterator = new StringCharacterIterator(aRegexFragment);
		    char character =  iterator.current();
		    while (character != CharacterIterator.DONE ){
		      /*
		      * All literals need to have backslashes doubled.
		      */
		      if (character == '.') {
		        result.append("\\.");
		      }
		      else if (character == '\\') {
		        result.append("\\\\");
		      }
		      else if (character == '?') {
		        result.append("\\?");
		      }
		      else if (character == '*') {
		        result.append("\\*");
		      }
		      else if (character == '+') {
		        result.append("\\+");
		      }
		      else if (character == '&') {
		        result.append("\\&");
		      }
		      else if (character == ':') {
		        result.append("\\:");
		      }
		      else if (character == '{') {
		        result.append("\\{");
		      }
		      else if (character == '}') {
		        result.append("\\}");
		      }
		      else if (character == '[') {
		        result.append("\\[");
		      }
		      else if (character == ']') {
		        result.append("\\]");
		      }
		      else if (character == '(') {
		        result.append("\\(");
		      }
		      else if (character == ')') {
		        result.append("\\)");
		      }
		      else if (character == '^') {
		        result.append("\\^");
		      }
		      else if (character == '$') {
		        result.append("\\$");
		      }
		      else {
		        //the char is not a special one
		        //add it to the result as is
		        result.append(character);
		      }
		      character = iterator.next();
		    }
		    return result.toString();
		  }
	  /** 
	   * Recreates the full URL that originally got the web client to the given 
	   * request.  This takes into account changes to the request due to request 
	   * dispatching. copied from: http://issues.apache.org/bugzilla/show_bug.cgi?id=28222
	   *
	   * <p>Note that if the protocol is HTTP and the port number is 80 or if the
	   * protocol is HTTPS and the port number is 443, then the port number is not 
	   * added to the return string as a convenience.</p>
	   */
	  public final static String getReturnURL(HttpServletRequest request)
	  {
	      if (request == null)
	      {
	          throw new IllegalArgumentException("Cannot take null parameters.");
	      }
	      
	      String scheme = request.getScheme();
	      String serverName = request.getServerName();
	      int serverPort = request.getServerPort();
	      
	      //try to get the forwarder value first, only if it's empty fall back to the  current value
	      String requestUri =
	    	  (String)request.getAttribute("javax.servlet.forward.request_uri");
	      requestUri = (requestUri == null) ? request.getRequestURI() : requestUri;
	   
	      //try to get the forwarder value first, only if it's empty fall back to the current value 
	      String queryString = (String)request.getAttribute("javax.servlet.forward.query_string");
	      queryString = (queryString == null) ? request.getQueryString() : queryString;

	      StringBuffer buffer = new StringBuffer();
	      buffer.append(scheme);
	      buffer.append("://");
	      buffer.append(serverName);
	      
	      //if not http:80 or https:443, then add the port number
	      if(
	          !(scheme.equalsIgnoreCase("http") && serverPort == 80) &&
	          !(scheme.equalsIgnoreCase("https") && serverPort == 443)
	      )
	      {
	          buffer.append(":");
	          buffer.append(String.valueOf(serverPort));
	      }
	      
	      buffer.append(requestUri);
	      
	      if (queryString != null)
	      {
	          buffer.append("?");
	          buffer.append(queryString);
	      }
	      
	      return buffer.toString();
	  }
}

class CASFilterChain implements FilterChain {

	private boolean doNext = false;

	public void doFilter(ServletRequest request, ServletResponse response) {
		doNext = true;
	}

	public boolean isDoNext() {
		return doNext;
	}
}

class CASUser extends AppUser {

	public CASUser(String userName){
		this.authenticatedUserName = userName;
	}
	private String authenticatedUserName ;
	
	public String getUserName(){
		return authenticatedUserName; 
	}
}