package eionet.gdem.exceptions;

public class DCMException extends Exception{
	   
	private String errorCode;
	
	public String getErrorCode() {
	      return errorCode;
	}

   public DCMException(String errorCode, String message) {
	      super(message);
	      this.errorCode = errorCode;
	   }

   public DCMException(String errorCode) {	      
	      this.errorCode = errorCode;
	   }   
   
}
