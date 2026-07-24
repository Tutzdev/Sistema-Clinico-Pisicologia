package br.com.agendamento.psicologia.config;

import br.com.agendamento.psicologia.enums.RoleEnum;
import br.com.agendamento.psicologia.repository.UsuarioRepository;
import br.com.agendamento.psicologia.security.CustomUserDetailsService;
import br.com.agendamento.psicologia.security.JwtAuthenticationFilter;
import br.com.agendamento.psicologia.security.JwtService;
import br.com.agendamento.psicologia.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            SecurityConfig.class
    );

    private static final int TAMANHO_MINIMO_CHAVE_JWT = 32;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
    }

    @Bean
    public SecretKey jwtSecretKey(
            @Value("${security.jwt.secret}") String secret
    ) {
        byte[] secretBytes;

        try {
            secretBytes = Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException(
                    "JWT_SECRET deve estar codificada em Base64.",
                    exception
            );
        }

        if (secretBytes.length < TAMANHO_MINIMO_CHAVE_JWT) {
            throw new IllegalStateException(
                    "JWT_SECRET deve possuir pelo menos 32 bytes."
            );
        }

        return new SecretKeySpec(
                secretBytes,
                "HmacSHA256"
        );
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey secretKey) {
        return NimbusJwtEncoder
                .withSecretKey(secretKey)
                .algorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder(
            SecretKey secretKey,
            @Value("${security.jwt.issuer}") String issuer
    ) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        jwtDecoder.setJwtValidator(
                JwtValidators.createDefaultWithIssuer(issuer)
        );

        return jwtDecoder;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider authenticationProvider =
                new DaoAuthenticationProvider(userDetailsService);

        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public ApplicationRunner criarAdministradorInicial(
            UsuarioRepository usuarioRepository,
            UsuarioService usuarioService,
            @Value("${security.initial-admin.email:}")
            String email,
            @Value("${security.initial-admin.password:}")
            String senha
    ) {
        return arguments -> {
            if (usuarioRepository.existsByRole(RoleEnum.ADMIN)) {
                return;
            }

            if (email == null
                    || email.isBlank()
                    || senha == null
                    || senha.isBlank()) {
                throw new IllegalStateException(
                        "Configure ADMIN_EMAIL e ADMIN_PASSWORD "
                                + "para criar o primeiro administrador."
                );
            }

            usuarioService.criarAdministradorInicial(
                    email,
                    senha
            );

            LOGGER.info(
                    "Administrador inicial criado com sucesso."
            );
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtService jwtService,
            CustomUserDetailsService userDetailsService
    ) throws Exception {
        JwtAuthenticationFilter jwtFilter =
                new JwtAuthenticationFilter(
                        jwtService,
                        userDetailsService
                );

        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                new HttpStatusEntryPoint(
                                        HttpStatus.UNAUTHORIZED
                                )
                        )
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/",
                                "/login",
                                "/api/auth/**",
                                "/api/public/**",
                                "/agendar/**",
                                "/css/**",
                                "/js/**",
                                "/favicon.ico",
                                "/error"
                        ).permitAll()

                        .requestMatchers("/api/usuarios/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/profissionais/**")
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/pacientes",
                                "/api/agendamentos"
                        ).permitAll()

                        .requestMatchers("/logout")
                        .authenticated()

                        .requestMatchers("/profissional/**")
                        .hasRole("PROFISSIONAL")

                        .requestMatchers("/api/profissional/me/**")
                        .hasRole("PROFISSIONAL")

                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/pacientes/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/agendamentos/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/**")
                        .authenticated()

                        .anyRequest()
                        .authenticated()
                )
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
