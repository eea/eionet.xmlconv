package eionet.gdem.security.repository;

import eionet.gdem.security.model.ApiUser;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
public interface ApiUserRepository {
      public ApiUser findByUsername(String username);
}
