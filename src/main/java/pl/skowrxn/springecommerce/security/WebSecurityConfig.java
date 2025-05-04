package pl.skowrxn.springecommerce.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.skowrxn.springecommerce.entity.Role;
import pl.skowrxn.springecommerce.entity.RoleType;
import pl.skowrxn.springecommerce.entity.User;
import pl.skowrxn.springecommerce.repository.RoleRepository;
import pl.skowrxn.springecommerce.repository.UserRepository;
import pl.skowrxn.springecommerce.security.service.UserDetailsServiceImpl;

import java.util.Set;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final AuthEntryPointJWT unauthorizedHandler;
    private final UserDetailsServiceImpl userDetailsService;

    public WebSecurityConfig(AuthEntryPointJWT unauthorizedHandler, UserDetailsServiceImpl userDetailsService) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthTokenFilter authTokenFilter(JWTUtils jwtUtils, UserDetailsService userDetailsService) {
        return new AuthTokenFilter(jwtUtils, userDetailsService);
    }

    @Bean
        public DaoAuthenticationProvider daoAuthenticationProvider() {
            DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
            provider.setUserDetailsService(this.userDetailsService);
            provider.setPasswordEncoder(this.passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthTokenFilter authTokenFilter) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler));
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(auth ->
            auth.requestMatchers("/auth/**").permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll() // Swagger UI ścieżki otwarte
                    .requestMatchers("/v3/api-docs/**").permitAll()
                    .anyRequest().authenticated()
        );

        http.authenticationProvider(this.daoAuthenticationProvider());
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//          return (web -> web.ignoring().requestMatchers("/v2/api-docs",
//                "/configuration/ui",
//                "/swagger-resources/**",
//                "/configuration/security",
//                "/swagger-ui.html",
//                "/webjars/**"));
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }


}
