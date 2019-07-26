package com.web;

import com.web.resolver.UserArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@SpringBootApplication
public class BootWebApplication implements WebMvcConfigurer{

	@Autowired
	private UserArgumentResolver userArgumentResolver;

	public static void main(String[] args) {
		SpringApplication.run(BootWebApplication.class, args);
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(userArgumentResolver);
	}

	/*@Configuration
	@EnableGlobalMethodSecurity(prePostEnabled = true)
	@EnableWebSecurity
	static class SecurityConfiguration extends WebSecurityConfigurerAdapter {

		@Bean
		InMemoryUserDetailsManager userDetailsManager() {
			User.UserBuilder commonUser = User.builder().name("commonUser");
			User.UserBuilder havi =  User.builder().name("havi");

			List<UserDetails> userDetailsList = new ArrayList<>();
			userDetailsList.add(commonUser.password("{noop}common").roles("USER").build());
			userDetailsList.add(havi.password("{noop}test").roles("USER", "ADMIN").build());

			return new InMemoryUserDetailsManager(userDetailsList);
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			CorsConfiguration configuration = new CorsConfiguration();
			configuration.addAllowedOrigin("*");
			configuration.addAllowedMethod("*");
			configuration.addAllowedHeader("*");
			UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
			source.registerCorsConfiguration("/**", configuration);

			http.httpBasic()
					.and().authorizeRequests()
					.antMatchers(HttpMethod.POST, "/Boards/**").hasRole("ADMIN")
					.anyRequest().permitAll()
					.and().cors().configurationSource(source)
					.and().csrf().disable();
		}
	}*/
	}
