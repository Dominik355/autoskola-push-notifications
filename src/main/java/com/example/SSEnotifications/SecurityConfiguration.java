
package com.example.SSEnotifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private DiscoveryClient discoveryClient;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        
        http.csrf().disable().authorizeRequests()
                .antMatchers("/notification/*", "/logOut/*").permitAll()
                .anyRequest().hasIpAddress(getServerAddress())
                .and().csrf().disable();

    }   
    
    private String getServerAddress() {
        String server = discoveryClient.getInstances("autoskola-program").get(0).getHost();
        if(server.equals("localhost")) {
            server = "127.0.0.1";
        }
        System.out.println("Server address: "+server);
        return server;
    }
    
}
