package eionet.gdem;

public class GDEMException extends Exception {
/*  public GDEMException(int code, String msg)  {
			super(code, msg);
      System.err.println("GDEMException occured with reason <<" + code+ ": " + msg + ">>");
	} */
  public GDEMException(String msg)  {
      super(msg);
      System.err.println("GDEMException occured with reason <<" + msg + ">>");
    }
  
}