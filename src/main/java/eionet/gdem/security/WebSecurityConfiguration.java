package eionet.gdem.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
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
  @ComponentScan
  @Order(1)
  public static class ApiSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    private EntryPointUnauthorizedHandler unauthorizedHandler;

    @Autowired
    @Qualifier("apiuserdetailsservice")
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
              .antMatcher("/restapi/**")
              .csrf()
              .disable()
              .exceptionHandling()
              .authenticationEntryPoint(this.unauthorizedHandler)
              .and()
              .sessionManagement()
              .sessionCreationPolicy(SessionCreationPolicy.NEVER)
              .and()
              .authorizeRequests()
              .antMatchers("/restapi/**").permitAll();

      // Custom JWT based authentication
      httpSecurity
              .addFilter(authenticationTokenFilterBean());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
      web
              .debug(true);
    }
  }

  @Configuration
  @ComponentScan
  public static class WebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

    @Bean
    public WebPreAuthenticationFilter webPreAuthenticationFilterBean() throws Exception {
      WebPreAuthenticationFilter webPreAuthenticationFilter = new WebPreAuthenticationFilter();
      webPreAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
      return webPreAuthenticationFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
              .antMatcher("/**")
              .authorizeRequests()
              .antMatchers("/**").permitAll()
              .antMatchers("/projects/*").hasRole("v")
              .and()
              .antMatcher("/RpcRouter/**")
              .csrf()
              .disable().authorizeRequests().antMatchers("/RpcRouter/**").permitAll();
      /*WebAuthenticationFilter webAuthenticationFilter = new WebAuthenticationFilter();
      webAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());*/
      http.addFilter(webPreAuthenticationFilterBean());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
          web
            .debug(true);
    }

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
      return super.authenticationManagerBean();
    }

/*
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.authenticationProvider(authenticationProvider);
    }
*/

  }

}