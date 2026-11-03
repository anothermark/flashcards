package com.cards.flash.security; // Make sure this matches your project's actual package path

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .authorizeHttpRequests(auth -> auth
	            // 1. Allow everyone to see the home page, bibliography, faq, and static folders
	            .requestMatchers("/", "/index.html", "/bibliography.html", "/faq.html", "/css/**", "/js/**").permitAll()
	            
	            // 2. Lock down the cardmaker page completely
	            .requestMatchers("/cardmaker", "/cardmaker/**").authenticated()
	            
	            // 3. Keep any other unexpected routes open
	            .anyRequest().permitAll()
	        )
	        // 4. Use Spring's automatic, server-side login form
	        .formLogin(form -> form
	            .defaultSuccessUrl("/cardmaker", true)
	            .permitAll()
	        )
	        // 5. Allow users to sign out
	        .logout(logout -> logout
	            .logoutSuccessUrl("/")
	            .permitAll()
	        )
	        // =================================================================
	        // ADD THIS NEW SECTION BELOW TO FIX THE FIREFOX IFRAME PRIVACY BLOCK
	        // =================================================================
	       // You downgraded Spring's strict default setting from DENY (nobody can ever 
	       // frame this app) to SAMEORIGIN (this app is allowed to frame itself).
	        .headers(headers -> headers
	            .frameOptions(frame -> frame.sameOrigin())
	        );

	    return http.build();
	}

	@Bean
    public UserDetailsService userDetailsService() {
        // 1. Fetch the password securely from the hosting environment
        String adminPassword = System.getenv("APP_ADMIN_PASSWORD");
        
        // 2. Safe protection block for local development or missing keys
        if (adminPassword == null || adminPassword.isEmpty()) {
            // This is just a placeholder for local coding. 
            // It protects your live production website from being hacked via GitHub.
            adminPassword = "LOCAL_DEV_PASSWORD_ONLY"; 
        }

        // 3. Build the secure server-side login credentials
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("guest")
                .password(adminPassword)
                .roles("GUEST")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }
}