package eionet.gdem.dcm.business;

import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.tee.uit.client.ServiceClientIF;
import com.tee.uit.client.ServiceClients;

import eionet.gdem.Properties;


public  class DDServiceClient {

	protected String serviceName = null;
	protected String serviceUrl  = null;
	//protected String serviceUsr  = null;
	//protected String servicePsw  = null;
	protected ServiceClientIF client = null;
	
	protected void load() throws Exception {
		
		//if (Util.voidStr(serviceName) || Util.voidStr(serviceUrl))
		if(serviceName==null || serviceName.equals("") || serviceUrl ==null || serviceUrl.equals(""))
			throw new Exception("serviceName or serviceUrl is missing!");
		
		client = ServiceClients.getServiceClient(serviceName, serviceUrl);
		//if (!Util.voidStr(serviceUsr) && !Util.voidStr(serviceUsr))
		//	client.setCredentials(serviceUsr,servicePsw);
	}
	
	protected void getProps(){
		
		 serviceName = Properties.invServName;
		 serviceUrl = Properties.invServUrl;
	}
	
	protected Object execute(String method, Vector params) throws Exception{
		
		if (client == null) load();
		return client.getValue(method, params);
	}
	
	public  void execute(HttpServletRequest req) throws Exception {
		
	}
	 public static void main( String args[] ) {
		 DDServiceClient d = new DDServiceClient();
		 try{
			 Vector b = new Vector();
			 d.getProps();
			 d.load();
			 Object res = d.execute("getDSTables", b);
	        List list = (List) res;
	        for(int i=0; i<list.size(); i++)
	        {
	          Object o = list.get(i);
	          System.out.println( i + " - " + o );
	        }
			 
		 }catch(Exception e){e.printStackTrace();}
		 
		 
	 }
	 public static List getDDTables() {
		 DDServiceClient d = new DDServiceClient();
		 List list = null;
		 try{
			 Vector b = new Vector();
			 d.getProps();
			 d.load();
			 Object res = d.execute("getDSTables", b);
	        list = (List) res;
/*	        for(int i=0; i<list.size(); i++)
	        {
	          Object o = list.get(i);
	          System.out.println( i + " - " + o );
	        }
	*/		 
		 }catch(Exception e){e.printStackTrace();}
		 return list;
		 
	 }
}
