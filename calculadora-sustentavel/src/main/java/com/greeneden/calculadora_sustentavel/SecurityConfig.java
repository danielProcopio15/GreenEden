package com.greeneden.calculadora_sustentavel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                // Rotas públicas
                .requestMatchers(
                    "/", "/index",
                    "/calculadora", "/calculadora/**",
                    "/calcular",
                    "/resultado", "/resultado/**",
                    "/compra", "/compra/**",
                    "/contato", "/contato/**",
                    "/confirmacao", "/confirmacao/**",
                    "/beneficios", "/beneficios/**",
                    "/seguranca", "/seguranca/**",
                    "/auth/**",
                    "/cadastro", "/cadastro/**",
                    "/login", "/login/**"
                ).permitAll()
                // Recursos estáticos
                .requestMatchers(
                    "/css/**", "/js/**", "/images/**",
                    "/logo.png", "/static/**", "/webjars/**"
                ).permitAll()
                // H2 console (apenas desenvolvimento)
                .requestMatchers("/h2-console/**").permitAll()
                // Portal - acesso controlado manualmente via HttpSession em PortalController
                .requestMatchers("/portal/**").permitAll()
                // Qualquer outra rota também pública
                .anyRequest().permitAll()
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.disable())
            );

        return http.build();
    }
}