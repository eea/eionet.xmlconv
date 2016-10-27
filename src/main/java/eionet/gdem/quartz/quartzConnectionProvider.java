package eionet.gdem.quartz;

import eionet.gdem.SpringApplicationContext;
import eionet.gdem.services.db.dao.mysql.MySqlBaseDao;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Aris Katsanas <aka@eworx.gr>
 */
public class quartzConnectionProvider implements org.quartz.utils.ConnectionProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(MySqlBaseDao.class);

    private static class DataSourceHolder {
        private static final DataSource DATASOURCE;
        static {
            try {
                DATASOURCE = (DataSource) SpringApplicationContext.getBean("quartzDataSource");
            } catch (Exception e) {
                LOGGER.error( "quartzDataSource" , e );
                throw new ExceptionInInitializerError(e);
            }
        }
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return DataSourceHolder.DATASOURCE.getConnection();
    }

    @Override
    public void shutdown() throws SQLException {

    }

    @Override
    public void initialize() throws SQLException {
        
    }
    
}
