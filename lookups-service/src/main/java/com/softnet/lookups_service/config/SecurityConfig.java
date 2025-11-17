package com.softnet.lookups_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                // .requestMatchers(HttpMethod.POST, "/api/v1/currencies/**").permitAll()
                // .requestMatchers("/api/v1/currencies/**").permitAll()
                // .anyRequest().authenticated()
                .anyRequest().permitAll() // allow all requests
                )
                // .oauth2ResourceServer(oauth2 -> oauth2
                // .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                // );
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_"); // optional: prefix roles with "ROLE_"
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles"); // claim in JWT with user roles

        JwtAuthenticationConverter jwtAuthConverter = new JwtAuthenticationConverter();
        jwtAuthConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthConverter;
    }
}
