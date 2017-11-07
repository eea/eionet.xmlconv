package eionet.gdem.security.service.impl;

import eionet.gdem.security.repository.ApiUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
@Service
public class ApiUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    ApiUserRepository apiUserRepository;
    
    @Override
    public UserDetails loadUserByUsername(String string) throws UsernameNotFoundException {
        return apiUserRepository.findByUsername(string);
    }
}
