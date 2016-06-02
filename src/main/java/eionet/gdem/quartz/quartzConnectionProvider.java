package eionet.gdem.quartz;

import eionet.gdem.SpringApplicationContext;
import eionet.gdem.logging.Markers;
import eionet.gdem.services.db.dao.mysql.MySqlBaseDao;
import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Aris Katsanas <aka@eworx.gr>
 */
public class quartzConnectionProvider implements org.quartz.utils.ConnectionProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(MySqlBaseDao.class);

    private static DataSource ds = null;
 
    @Override
    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    @Override
    public void shutdown() throws SQLException {
        
    }

    @Override
    public void initialize() throws SQLException {
        try {
            InitialContext ctx = new InitialContext();
            ds = (DataSource) SpringApplicationContext.getBean("quartzDataSource");
        } catch (NamingException e) {
            LOGGER.error(Markers.fatal, "Initialization of datasource failed: ", e);
        }
    }
    
}
