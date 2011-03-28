package eionet.gdem.services.db.dao;

import java.sql.SQLException;
import java.util.Vector;

public interface IHostDao extends IDbSchema{

      /**
      * Adds a new Host to the database
      * @param hostName - host name  (http://eionet.eea.eu.int"
      * @param userName - username
      * @param pwd - password
      * @return The ID of the added host
      */
      public String addHost(String hostName,  String userName, String pwd) throws SQLException;

      /**
      * Updates a Host properties in the database
      * @param host_id - id from database, used as a constraint
      * @param hostName - host name  (http://eionet.eea.eu.int"
      * @param userName - username
      * @param pwd - password
      */
      public void updateHost(String hostId, String hostName,  String userName, String pwd) throws SQLException;

      /**
      * Deletes the Host from the database
      * @param host_id - id from database, used as a constraint
      */
      public void removeHost(String hostId) throws SQLException;

    /**
      * returns hosts from database
      * @param host - if empty, then all fields are return
      *             - numeric id from database
      *             - host name as string - wildcard search is performed
      * @return Vector contining all fields from T_HOST table
      */

      public Vector getHosts(String host) throws SQLException;

    /**
      * returns conversion types from database
      * @param host - if empty, then all fields will be returned
      *             - conv_ty as string - wildcard search is performed
      * @return Vector contining all fields as HashMaps from T_CONVTYPET table
      */

}
