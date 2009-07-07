/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 * 
 * The Original Code is Content Registry 2.0.
 * 
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 * 
 * Contributor(s):
 * Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.utils.system;

import junit.framework.TestCase;

/**
 * JUnit test test System Command functionality.
 * 
 * @author Enriko Käsper, Tieto Estonia
 * SysCommandExecutorTest
 */

public class SysCommandExecutorTest extends TestCase{

	/**
	 * The method tests, if the system is able to execute some simple commands
	 * 
	 * @throws Exception
	 */
	
    public void testCommand() throws Exception{
    	   	
    	SysCommandExecutor exe = new SysCommandExecutor();
	    exe.setOutputLogDevice(new LogDevice());
	    exe.setErrorLogDevice(new LogDevice());
    	exe.setTimeout(20000L);
    	int status = exe.runCommand("echo OK");
    	String out = exe.getCommandOutput();
    	
    	assertEquals(status, 0);
    	assertEquals(out, "OK" + System.getProperty("line.separator"));
    }
	/**
	 * The method tests, if it's possible to kill the process after timeout
	 * 
	 * @throws Exception
	 */
	
    public void testCommandTimeout() throws Exception{
    
    	Exception eTimeout=null; 
    	SysCommandExecutor exe = new SysCommandExecutor();
	    exe.setOutputLogDevice(new LogDevice());
	    exe.setErrorLogDevice(new LogDevice());
    	exe.setTimeout(1000L); //1 second

    	try{
    		int status = exe.runCommand("sleep 3");
    	}
    	catch(RuntimeException e){
    		eTimeout=e;
    	}
    	// asset the exception object
    	assertNotNull("No expected exception", eTimeout);
    }
 }
