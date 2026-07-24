package br.com.agendamento.psicologia.controller;

import br.com.agendamento.psicologia.dto.LoginRequestDTO;
import br.com.agendamento.psicologia.dto.LoginResponseDTO;
import br.com.agendamento.psicologia.entity.Usuario;
import br.com.agendamento.psicologia.security.JwtService;
import br.com.agendamento.psicologia.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public AuthController(
            AuthenticationManager authenticationManager,
            UsuarioService usuarioService,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
    }

    @GetMapping("/login")
    public String paginaLogin() {
        return "auth/login";
    }

    @PostMapping("/api/auth/login")
    @ResponseBody
    public LoginResponseDTO login(
            @Valid @RequestBody LoginRequestDTO request
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

        return LoginResponseDTO.of(
                token,
                jwtService.getTempoExpiracaoEmSegundos(),
                usuario
        );
    }
}