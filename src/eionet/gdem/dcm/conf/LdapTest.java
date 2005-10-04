package eionet.gdem.dcm.conf;

import java.util.Hashtable;
import java.util.ResourceBundle;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

public class LdapTest {

	private static LoggerIF _logger=GDEMServices.getLogger();
    private String url;
	
    public LdapTest(String url) {
        this.url = url;
    }	
	
    protected DirContext getDirContext() throws NamingException {
       Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, url);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        //env.put(Context.SECURITY_PRINCIPAL, username);
        //env.put(Context.SECURITY_CREDENTIALS, password);
        DirContext ctx = new InitialDirContext(env);
        return ctx;
    }
    
    protected void closeContext(DirContext ctx) throws NamingException {
        if(ctx!=null) {
            ctx.close();
        }		
    }
	
	
	public boolean test(){
		try {
			DirContext ctx = getDirContext();
			closeContext(ctx);
			return true;
		}catch (Exception e)
		{
			 _logger.debug("Testing ldap connection failed!");
			 _logger.debug(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}	
	
	
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//ldapTest
		LdapTest lt = new LdapTest("ldap://192.168.0.80:389/");

		boolean t = lt.test();

	}

}
