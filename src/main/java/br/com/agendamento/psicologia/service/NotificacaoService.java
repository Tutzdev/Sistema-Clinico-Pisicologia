package br.com.agendamento.psicologia.service;

import br.com.agendamento.psicologia.entity.Agendamento;
import br.com.agendamento.psicologia.entity.Notificacao;
import br.com.agendamento.psicologia.enums.TipoNotificacaoEnum;
import br.com.agendamento.psicologia.repository.NotificacaoRepository;
import br.com.agendamento.psicologia.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificacaoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            NotificacaoService.class
    );

    private static final int LIMITE_TENTATIVAS = 3;

    private final NotificacaoRepository notificacaoRepository;
    private final EmailService emailService;

    public NotificacaoService(
            NotificacaoRepository notificacaoRepository,
            EmailService emailService
    ) {
        this.notificacaoRepository = notificacaoRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void registrarAgendamentoCriado(
            Agendamento agendamento
    ) {
        registrar(
                agendamento,
                TipoNotificacaoEnum.AGENDAMENTO_CRIADO,
                "Agendamento recebido",
                """
                Olá, %s.

                Seu agendamento com %s foi recebido para %s.
                Aguarde a confirmação do profissional.
                """.formatted(
                        agendamento.getPaciente().getNome(),
                        agendamento.getProfissional().getNome(),
                        formatarHorario(agendamento)
                )
        );
    }

    @Transactional
    public void registrarAgendamentoConfirmado(
            Agendamento agendamento
    ) {
        registrar(
                agendamento,
                TipoNotificacaoEnum.AGENDAMENTO_CONFIRMADO,
                "Agendamento confirmado",
                """
                Olá, %s.

                Seu agendamento com %s foi confirmado para %s.
                """.formatted(
                        agendamento.getPaciente().getNome(),
                        agendamento.getProfissional().getNome(),
                        formatarHorario(agendamento)
                )
        );
    }

    @Transactional
    public void registrarAgendamentoCancelado(
            Agendamento agendamento
    ) {
        registrar(
                agendamento,
                TipoNotificacaoEnum.AGENDAMENTO_CANCELADO,
                "Agendamento cancelado",
                """
                Olá, %s.

                Seu agendamento com %s, marcado para %s, foi cancelado.
                """.formatted(
                        agendamento.getPaciente().getNome(),
                        agendamento.getProfissional().getNome(),
                        formatarHorario(agendamento)
                )
        );
    }

    @Scheduled(
            initialDelayString =
                    "${notificacao.processamento-atraso-inicial-ms:30000}",
            fixedDelayString =
                    "${notificacao.processamento-intervalo-ms:60000}"
    )
    @Transactional
    public void processarPendentes() {
        List<Notificacao> notificacoes =
                notificacaoRepository
                        .findTop20ByEnviadaFalseAndTentativasLessThanOrderByDataCriacaoAsc(
                                LIMITE_TENTATIVAS
                        );

        notificacoes.forEach(this::enviar);
    }

    private void registrar(
            Agendamento agendamento,
            TipoNotificacaoEnum tipo,
            String assunto,
            String mensagem
    ) {
        String destinatario = agendamento.getPaciente().getEmail();

        if (destinatario == null || destinatario.isBlank()) {
            return;
        }

        if (notificacaoRepository.existsByAgendamentoIdAndTipo(
                agendamento.getId(),
                tipo
        )) {
            return;
        }

        Notificacao notificacao = Notificacao.criar(
                agendamento,
                tipo,
                destinatario,
                assunto,
                mensagem
        );

        notificacaoRepository.save(notificacao);
    }

    private void enviar(Notificacao notificacao) {
        try {
            emailService.enviar(
                    notificacao.getDestinatario(),
                    notificacao.getAssunto(),
                    notificacao.getMensagem()
            );

            notificacao.registrarEnvio();
        } catch (RuntimeException exception) {
            notificacao.registrarFalha(exception.getMessage());

            LOGGER.warn(
                    "Falha ao enviar a notificação {}.",
                    notificacao.getId(),
                    exception
            );
        }
    }

    private String formatarHorario(Agendamento agendamento) {
        return DateUtils.formatarDataHora(
                agendamento.getHorario().getDataHora()
        );
    }
}