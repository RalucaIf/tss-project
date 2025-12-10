package echipa13.calatorii.config;

import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;

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
                        .anyRequest().permitAll() // permit tot temporar
                )
                .formLogin(form -> form
                        .loginProcessingUrl("/auth/login") // endpoint POST pentru login
                        .usernameParameter("usernameOrEmail")        // câmp email din modal
                        .passwordParameter("password")// câmp parola din modal
                        .defaultSuccessUrl("/Itravel", true)
                        .permitAll()

                )
                .logout(logout -> logout
                        .logoutUrl("/logout")          // URL pentru logout
                        .logoutSuccessUrl("/Itravel")  // unde să fie redirect după logout
                        .invalidateHttpSession(true)   // distruge sesiunea
                        .deleteCookies("JSESSIONID")   // șterge cookie-ul de sesiune
                );

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

