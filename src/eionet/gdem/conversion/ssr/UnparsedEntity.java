package eionet.gdem.conversion.ssr;

public class UnparsedEntity {

  private String name;
  private String publicID;
  private String systemID;
  private String notationName;
  
  public UnparsedEntity(String name, String publicID, 
   String systemID, String notationName) {

    this.name = name;
    this.publicID = publicID;
    this.systemID = systemID;
    this.notationName = notationName;
    
  }
   
  public String getName() {
    return this.name;
  }
    
  public String getSystemID() {
    return this.systemID;
  }
    
  public String getPublicID() {
    return this.publicID;
  }
    
  public String getNotationName() {
    return this.notationName;
  }

  public boolean equals(Object o) {
    
    if (o instanceof UnparsedEntity) {
      UnparsedEntity entity = (UnparsedEntity) o;
      if (publicID == null) {
        return name.equals(entity.name) 
         && systemID.equals(entity.systemID)
         && notationName.equals(entity.notationName);
      }
      else {
        return name.equals(entity.name) 
         && systemID.equals(entity.systemID)
         && publicID.equals(entity.publicID)
         && notationName.equals(entity.notationName);
      }
    }
    return false;
    
  }
    
  public int hashCode() {
    
    if (publicID == null) {
      return name.hashCode() ^ systemID.hashCode() 
       ^ notationName.hashCode();
    }
    else {
      return name.hashCode() ^ publicID.hashCode() 
       ^ systemID.hashCode() ^ notationName.hashCode();
    }
    
  }

  public String toString() {
    
    StringBuffer result = new StringBuffer(name);
    if (publicID == null) {
      result.append(" PUBLIC "); 
      result.append(publicID);
    }
    else {
      result.append(" SYSTEM "); 
    }
    result.append(" ");
    result.append(systemID);

    return result.toString();
  }
    
}
