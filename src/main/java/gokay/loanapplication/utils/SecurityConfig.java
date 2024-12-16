package gokay.loanapplication.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.disable())
				.authorizeHttpRequests(req -> req.requestMatchers("/public/api/*").permitAll()
						.requestMatchers("/h2-console/**").permitAll()
						.requestMatchers("/loan/api/admin/all/customers").hasRole("ADMIN")
						.anyRequest().authenticated()
						)
				.httpBasic(Customizer.withDefaults())
				.headers(hed -> hed.frameOptions().disable());

		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails user1 = User.builder().username("user1").password(passwordEncoder().encode("customerpass"))
				.roles("CUSTOMER").build();
		UserDetails user2 = User.builder().username("user2").password(passwordEncoder().encode("customerpass"))
				.roles("CUSTOMER").build();
		UserDetails user3 = User.builder().username("user3").password(passwordEncoder().encode("customerpass"))
				.roles("CUSTOMER").build();
		UserDetails admin1 = User.builder().username("admin1").password(passwordEncoder().encode("adminpass"))
				.roles("ADMIN").build();
		UserDetails admin2 = User.builder().username("admin2").password(passwordEncoder().encode("adminpass"))
				.roles("ADMIN").build();
		return new InMemoryUserDetailsManager(user1, user2, user3, admin1, admin2);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
