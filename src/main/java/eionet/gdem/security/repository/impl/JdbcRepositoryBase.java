package eionet.gdem.security.repository.impl;

import javax.sql.DataSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
public abstract class JdbcRepositoryBase extends NamedParameterJdbcDaoSupport {
    
    public JdbcRepositoryBase(DataSource dataSource) {
        super.setDataSource(dataSource);
    }
    
}
