package eionet.gdem;

public class GDEMException extends Exception{
  public GDEMException()  {
  }
  public GDEMException(String msg)  {
      super(msg);
        System.err.println("GDEMException occured with reason <<" + msg + ">>");
    }
  
}