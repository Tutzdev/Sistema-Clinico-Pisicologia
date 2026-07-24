package br.com.agendamento.psicologia.controller;

import br.com.agendamento.psicologia.dto.LoginRequestDTO;
import br.com.agendamento.psicologia.dto.LoginResponseDTO;
import br.com.agendamento.psicologia.entity.Usuario;
import br.com.agendamento.psicologia.security.JwtService;
import br.com.agendamento.psicologia.service.UsuarioService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Duration;

@Controller
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final boolean cookieSecure;

    public AuthController(
            AuthenticationManager authenticationManager,
            UsuarioService usuarioService,
            JwtService jwtService,
            @Value("${security.jwt.cookie-secure:false}")
            boolean cookieSecure
    ) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
        this.cookieSecure = cookieSecure;
    }

    @GetMapping("/login")
    public String paginaLogin() {
        return "auth/login";
    }

    @PostMapping("/api/auth/login")
    @ResponseBody
    public LoginResponseDTO login(
            @Valid @RequestBody LoginRequestDTO request,
            HttpServletResponse response
    ) {
        String email = request.emailNormalizado();

        authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        email,
                        request.senha()
                )
        );

        Usuario usuario = usuarioService.buscarPorEmail(email);
        String token = jwtService.gerarToken(usuario);
        long expiracao = jwtService.getTempoExpiracaoEmSegundos();

        adicionarCookieAutenticacao(
                response,
                token,
                expiracao
        );

        return LoginResponseDTO.of(
                token,
                expiracao,
                usuario
        );
    }

    @PostMapping("/admin/logout")
    public String logout(HttpServletResponse response) {
        ResponseCookie cookie = criarCookie(
                "",
                Duration.ZERO
        );

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );

        return "redirect:/login";
    }

    private void adicionarCookieAutenticacao(
            HttpServletResponse response,
            String token,
            long expiracaoEmSegundos
    ) {
        ResponseCookie cookie = criarCookie(
                token,
                Duration.ofSeconds(expiracaoEmSegundos)
        );

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );
    }

    private ResponseCookie criarCookie(
            String valor,
            Duration duracao
    ) {
        return ResponseCookie
                .from(JwtService.COOKIE_NAME, valor)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .path("/admin")
                .maxAge(duracao)
                .build();
    }
}