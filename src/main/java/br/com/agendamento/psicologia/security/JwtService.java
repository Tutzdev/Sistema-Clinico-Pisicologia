package br.com.agendamento.psicologia.security;

import br.com.agendamento.psicologia.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class JwtService {

    public static final String COOKIE_NAME = "ADMIN_TOKEN";

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final String issuer;
    private final Duration tokenDuration;

    public JwtService(
            JwtEncoder jwtEncoder,
            JwtDecoder jwtDecoder,
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.expiration-minutes}")
            long expirationMinutes
    ) {
        if (expirationMinutes <= 0) {
            throw new IllegalArgumentException(
                    "A duração do token deve ser maior que zero."
            );
        }

        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.issuer = issuer;
        this.tokenDuration = Duration.ofMinutes(expirationMinutes);
    }

    public String gerarToken(Usuario usuario) {
        Instant emitidoEm = Instant.now();
        Instant expiraEm = emitidoEm.plus(tokenDuration);

        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .type("JWT")
                .build();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(emitidoEm)
                .expiresAt(expiraEm)
                .subject(usuario.getEmail())
                .claim("usuarioId", usuario.getId())
                .claim("role", usuario.getRole().name())
                .build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }

    public Jwt decodificarToken(String token) {
        return jwtDecoder.decode(token);
    }

    public boolean pertenceAoUsuario(
            Jwt jwt,
            UserDetails userDetails
    ) {
        return jwt.getSubject().equalsIgnoreCase(
                userDetails.getUsername()
        ) && userDetails.isEnabled();
    }

    public long getTempoExpiracaoEmSegundos() {
        return tokenDuration.toSeconds();
    }
}