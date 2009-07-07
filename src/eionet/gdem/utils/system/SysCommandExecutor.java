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
 * The Original Code is XMLCONV.
 * 
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 * 
 * Contributor(s):
 * Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.utils.system;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eionet.gdem.Properties;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;
 
/**
 * Usage of following class can go as ...
 * <P><PRE><CODE>
 * 		SysCommandExecutor cmdExecutor = new SysCommandExecutor();
 * 		cmdExecutor.setOutputLogDevice(new LogDevice());
 * 		cmdExecutor.setErrorLogDevice(new LogDevice());
 * 		int exitStatus = cmdExecutor.runCommand(commandLine);
 * </CODE></PRE></P>
 * 
 * OR
 * 
 * <P><PRE><CODE>
 * 		SysCommandExecutor cmdExecutor = new SysCommandExecutor(); 		
 * 		int exitStatus = cmdExecutor.runCommand(commandLine);
 * 
 * 		String cmdError = cmdExecutor.getCommandError();
 * 		String cmdOutput = cmdExecutor.getCommandOutput(); 
 * </CODE></PRE></P> 
 */
public class SysCommandExecutor
{	
	private ILogDevice fOuputLogDevice = null;
	private ILogDevice fErrorLogDevice = null;
	private String fWorkingDirectory = null;
	private List fEnvironmentVarList = null;
	
	private StringBuffer fCmdOutput = null;
	private StringBuffer fCmdError = null;
	private AsyncStreamReader fCmdOutputThread = null;
	private AsyncStreamReader fCmdErrorThread = null;	
	
	private long timeout=0;
	
	public long getTimeout() {
		if(timeout==0) timeout=Properties.qaTimeout;
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	private static LoggerIF logger=GDEMServices.getLogger();

	public void setOutputLogDevice(ILogDevice logDevice)
	{
		fOuputLogDevice = logDevice;
	}
	
	public void setErrorLogDevice(ILogDevice logDevice)
	{
		fErrorLogDevice = logDevice;
	}
	
	public void setWorkingDirectory(String workingDirectory) {
		fWorkingDirectory = workingDirectory;
	}
	
	public void setEnvironmentVar(String name, String value)
	{
		if( fEnvironmentVarList == null )
			fEnvironmentVarList = new ArrayList();
		
		fEnvironmentVarList.add(new EnvironmentVar(name, value));
	}
	
	public String getCommandOutput() {		
		return fCmdOutput.toString();
	}
	
	public String getCommandError() {
		return fCmdError.toString();
	}
	
	public int runCommand(String commandLine) throws Exception
	{
		/* run command */
		Process process = runCommandHelper(commandLine);
		
		/* start output and error read threads */
		startOutputAndErrorReadThreads(process.getInputStream(), process.getErrorStream());

        // create and start a Worker thread which this thread will join for the timeout period 
        Worker worker = new Worker(process);
        worker.start();
        try
        {
            worker.join(getTimeout());
            Integer exitValue = worker.getExitValue();
            if (exitValue != null)
            {
                // the worker thread completed within the timeout period
                return exitValue;
            }

            // if we get this far then we never got an exit value from the worker thread as a result of a timeout 
            String errorMessage = "The command [" + commandLine + "] timed out.";
            logger.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
        catch (InterruptedException ex)
        {
            worker.interrupt();
            Thread.currentThread().interrupt();
            throw ex;
        }

	}	
	
	private Process runCommandHelper(String commandLine) throws IOException
	{
		Process process = null;	
		commandLine = validateSystemAndMassageCommand(commandLine);
		if( fWorkingDirectory == null )
			process = Runtime.getRuntime().exec(commandLine, getEnvTokens());
		else
			process = Runtime.getRuntime().exec(commandLine, getEnvTokens(), new File(fWorkingDirectory));
		
		return process;
	}
	
	private void startOutputAndErrorReadThreads(InputStream processOut, InputStream processErr)
	{
		fCmdOutput = new StringBuffer();
		fCmdOutputThread = new AsyncStreamReader(processOut, fCmdOutput, fOuputLogDevice, "OUTPUT");		
		fCmdOutputThread.start();
		
		fCmdError = new StringBuffer();
		fCmdErrorThread = new AsyncStreamReader(processErr, fCmdError, fErrorLogDevice, "ERROR");
		fCmdErrorThread.start();
	}
	
	private void notifyOutputAndErrorReadThreadsToStopReading()
	{
		fCmdOutputThread.stopReading();
		fCmdErrorThread.stopReading();
	}
	
	private String[] getEnvTokens()
	{
		if( fEnvironmentVarList == null )
			return null;
		
		String[] envTokenArray = new String[fEnvironmentVarList.size()];
		Iterator envVarIter = fEnvironmentVarList.iterator();
		int nEnvVarIndex = 0; 
		while (envVarIter.hasNext() == true)
		{
			EnvironmentVar envVar = (EnvironmentVar)(envVarIter.next());
			String envVarToken = envVar.fName + "=" + envVar.fValue;
			envTokenArray[nEnvVarIndex++] = envVarToken;
		}
		
		return envTokenArray;
	}	

    /**
     * Validates that the system is running a supported OS and returns a system-appropriate command line.
     * 
     * @param originalCommand
     * @return
     */
    private static String validateSystemAndMassageCommand(final String originalCommand)
    {
        // make sure that we have a command
        if (Utils.isNullStr(originalCommand) || (originalCommand.length() < 1))
        {
            String errorMessage = "Missing or empty command line parameter.";
            throw new RuntimeException(errorMessage);
        }

        // make sure that we are running on a supported system, and if so set the command line appropriately
        String massagedCommand;
        String osName = System.getProperty("os.name");
        if (osName.equals("Windows XP"))
        {
            massagedCommand = "cmd.exe /C " + originalCommand;
        }
        else if (osName.equals("Solaris") || osName.equals("SunOS") || osName.equals("Linux"))
        {
            massagedCommand = originalCommand;
        }
        else
        {
            String errorMessage = "Unable to run on this system which is not Solaris, Linux, or Windows XP (actual OS type: \'" +
                                  osName + "\').";
            throw new RuntimeException(errorMessage);
        }

        return massagedCommand;
    }
    
    /**
     * Thread class to be used as a worker
     */
    private static class Worker
        extends Thread
    {
        private final Process process;
        private Integer exitValue;

        Worker(final Process process)
        {
            this.process = process;
        }

        public Integer getExitValue()
        {
            return exitValue;
        }

        @Override
        public void run()
        {
            try
            {
                exitValue = process.waitFor();
            }
            catch (InterruptedException ignore)
            {
                return;
            }
        }
    }
 }
 