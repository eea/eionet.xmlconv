package eionet.gdem.security.repository.impl;

import eionet.gdem.security.model.ApiUser;
import eionet.gdem.security.repository.ApiUserRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
@Repository
public class ApiUserRepositoryImpl extends JdbcRepositoryBase implements ApiUserRepository {

    @Autowired
    public ApiUserRepositoryImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ApiUser findByUsername(String username) {

        String sql = "select * from T_API_USER where USERNAME = :username";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", username);
        List<ApiUser> results = this.getNamedParameterJdbcTemplate().query(sql, params, new ApiUserSetRowMapper());
        return results.isEmpty() ? null : results.get(0);
    }

    private static class ApiUserSetRowMapper implements RowMapper<ApiUser> {

        @Override
        public ApiUser mapRow(ResultSet rs, int i) throws SQLException {
            ApiUser user = new ApiUser();
            user.setEmail(rs.getString("EMAIL"));
            user.setId(rs.getLong("ID"));
            user.setEnabled(rs.getBoolean("ENABLED"));
            user.setUsername(rs.getString("USERNAME"));
            // user.setAuthorities(rs.getString("AUTHORITIES")); 
            return user;
        }
    }
}
