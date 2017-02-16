package eionet.gdem.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 * @author George Sofianos
 */
@EnableWebSecurity
/*@EnableGlobalMethodSecurity(prePostEnabled = true)*/
public class WebSecurityConfiguration {

  @Configuration
  @Order(1)
  public static class ApiSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    private EntryPointUnauthorizedHandler unauthorizedHandler;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
      authenticationManagerBuilder
              .userDetailsService(this.userDetailsService)
              .passwordEncoder(passwordEncoder());
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
      return super.authenticationManagerBean();
    }

    @Bean
    public AuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
      AuthenticationTokenFilter authenticationTokenFilter = new AuthenticationTokenFilter();
      authenticationTokenFilter.setAuthenticationManager(authenticationManagerBean());
      return authenticationTokenFilter;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
      httpSecurity
              .antMatcher("/restapi/*")
              .csrf()
              .disable()
              .exceptionHandling()
              .authenticationEntryPoint(this.unauthorizedHandler)
              .and()
              .sessionManagement()
              .sessionCreationPolicy(SessionCreationPolicy.NEVER)
              .and()
              .authorizeRequests()
              .antMatchers("/restapi/*").permitAll();

      // Custom JWT based authentication
      httpSecurity
              .addFilter(authenticationTokenFilterBean());
    }
  }

  @Configuration
  public static class WebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception {
      web
              /*.debug(true)*/
              .ignoring().antMatchers("/restapi/*");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
              .antMatcher("/*")
              .authorizeRequests()
              .antMatchers("/*").permitAll();
    }
  }

}