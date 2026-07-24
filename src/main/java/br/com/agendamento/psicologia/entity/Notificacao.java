package br.com.agendamento.psicologia.entity;

import br.com.agendamento.psicologia.enums.TipoNotificacaoEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "notificacoes",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_notificacao_agendamento_tipo",
                columnNames = {"agendamento_id", "tipo"}
        ),
        indexes = @Index(
                name = "idx_notificacao_pendente",
                columnList = "enviada, tentativas, data_criacao"
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notificacao {

    private static final int TAMANHO_MAXIMO_ERRO = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agendamento_id", nullable = false)
    private Agendamento agendamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TipoNotificacaoEnum tipo;

    @Column(nullable = false, length = 160)
    private String destinatario;

    @Column(nullable = false, length = 160)
    private String assunto;

    @Column(nullable = false, length = 2000)
    private String mensagem;

    @Column(nullable = false)
    private boolean enviada;

    @Column(nullable = false)
    private int tentativas;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_envio")
    private LocalDateTime dataEnvio;

    @Column(name = "ultima_tentativa")
    private LocalDateTime ultimaTentativa;

    @Column(name = "erro_envio", length = TAMANHO_MAXIMO_ERRO)
    private String erroEnvio;

    public static Notificacao criar(
            Agendamento agendamento,
            TipoNotificacaoEnum tipo,
            String destinatario,
            String assunto,
            String mensagem
    ) {
        Notificacao notificacao = new Notificacao();
        notificacao.agendamento = agendamento;
        notificacao.tipo = tipo;
        notificacao.destinatario = destinatario;
        notificacao.assunto = assunto;
        notificacao.mensagem = mensagem;
        notificacao.dataCriacao = LocalDateTime.now();
        notificacao.enviada = false;
        notificacao.tentativas = 0;

        return notificacao;
    }

    public void registrarEnvio() {
        LocalDateTime agora = LocalDateTime.now();

        this.enviada = true;
        this.dataEnvio = agora;
        this.ultimaTentativa = agora;
        this.erroEnvio = null;
        this.tentativas++;
    }

    public void registrarFalha(String mensagemErro) {
        this.enviada = false;
        this.ultimaTentativa = LocalDateTime.now();
        this.erroEnvio = limitarMensagemErro(mensagemErro);
        this.tentativas++;
    }

    private String limitarMensagemErro(String mensagemErro) {
        if (mensagemErro == null || mensagemErro.isBlank()) {
            return "Falha não identificada durante o envio.";
        }

        if (mensagemErro.length() <= TAMANHO_MAXIMO_ERRO) {
            return mensagemErro;
        }

        return mensagemErro.substring(0, TAMANHO_MAXIMO_ERRO);
    }
}