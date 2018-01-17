package arun.auth.server.security;

import arun.auth.server.custom.CustomAuthHeaderMatcher;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.requestMatcher(new CustomAuthHeaderMatcher("Authorization", "bearer"))
            .authorizeRequests()
            .antMatchers("/me", "/user").authenticated()
            .antMatchers("/me/name", "/user/name'").authenticated();
    }
}
