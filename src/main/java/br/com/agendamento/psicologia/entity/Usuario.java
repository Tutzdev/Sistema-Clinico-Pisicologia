package br.com.agendamento.psicologia.entity;

import br.com.agendamento.psicologia.enums.RoleEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "usuarios",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_usuario_profissional",
                columnNames = "profissional_id"
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(nullable = false, length = 255)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RoleEnum role;

    @Column(nullable = false)
    private boolean ativo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profissional_id")
    private Profissional profissional;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    public static Usuario criar(
            String email,
            String senha,
            RoleEnum role,
            Profissional profissional
    ) {
        Usuario usuario = new Usuario();
        LocalDateTime agora = LocalDateTime.now();

        usuario.email = email;
        usuario.senha = senha;
        usuario.role = role;
        usuario.profissional = profissional;
        usuario.ativo = true;
        usuario.criadoEm = agora;
        usuario.atualizadoEm = agora;

        return usuario;
    }

    public void alterarSenha(String novaSenha) {
        this.senha = novaSenha;
        atualizarDataModificacao();
    }

    public void ativar() {
        if (ativo) {
            return;
        }

        this.ativo = true;
        atualizarDataModificacao();
    }

    public void desativar() {
        if (!ativo) {
            return;
        }

        this.ativo = false;
        atualizarDataModificacao();
    }

    private void atualizarDataModificacao() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
