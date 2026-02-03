package echipa13.calatorii.config;

import com.openai.core.http.HttpMethod;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect; // ✅ CORECT


import echipa13.calatorii.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

//    private final CustomUserDetailsService userDetailsService;
//
//    @Autowired
//    public SecurityConfig(CustomUserDetailsService userDetailsService) {
//        this.userDetailsService = userDetailsService;
//    }

    @Bean
    public CustomUserDetailsService customUserDetailsService(UserRepository userRepository) {
        return new CustomUserDetailsService(userRepository);
    }

    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // === PUBLIC & STATIC ===
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/vendor/**", "/webjars/**").permitAll()
                        .requestMatchers("/", "/Itravel", "/About", "/Destinations",
                                "/Contact", "/Terms", "/Privacy", "/Error/**",
                                "/Tour_details", "/Tours").permitAll()
                        .requestMatchers(String.valueOf(HttpMethod.POST), "/auth/login").permitAll()
                        .requestMatchers("/logout").permitAll()
                                .requestMatchers(org.springframework.http.HttpMethod.GET, "/uploads/**").permitAll()
                                .requestMatchers(org.springframework.http.HttpMethod.GET, "/imagine/**").permitAll()
                        // === TRIPS: DOAR USER ===
                        .requestMatchers("/trips/**").hasRole("User")   // => necesită autoritate ROLE_USER
                                .requestMatchers("/users/**").hasRole("User")
                        .requestMatchers("/tours/buy/**").hasRole("User")
                        .requestMatchers("/tours/**").hasRole("User")
                        // === GHID: creare/administrare tours ===
                        .requestMatchers("/Itravel/new").hasRole("Guide") // => ROLE_GUIDE
                                .requestMatchers("/journal/**").authenticated()

                                .requestMatchers("/destinations/new").hasRole("Admin")

                                // DESTINATIONS (admin poate adăuga/edita/șterge țări)
//                        .requestMatchers("/Destinations/new", "/Destinations/edit/**", "/Destinations/delete/**").hasRole("Admin")

                        // restul – lasă-le publice ca până acum
                        .anyRequest().permitAll()
                )

                .formLogin(form -> form
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("usernameOrEmail")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/Itravel", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/Itravel")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .exceptionHandling(ex -> ex.accessDeniedPage("/error/403"));

        return http.build();
    }





    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }





}

