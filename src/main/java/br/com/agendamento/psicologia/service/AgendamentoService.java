package br.com.agendamento.psicologia.service;

import br.com.agendamento.psicologia.entity.Agendamento;
import br.com.agendamento.psicologia.entity.HorarioDisponivel;
import br.com.agendamento.psicologia.entity.Paciente;
import br.com.agendamento.psicologia.enums.StatusAgendamentoEnum;
import br.com.agendamento.psicologia.exception.BusinessException;
import br.com.agendamento.psicologia.exception.ResourceNotFoundException;
import br.com.agendamento.psicologia.repository.AgendamentoRepository;
import br.com.agendamento.psicologia.repository.HorarioDisponivelRepository;
import br.com.agendamento.psicologia.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final HorarioDisponivelRepository horarioRepository;
    private final PacienteRepository pacienteRepository;
    private final NotificacaoService notificacaoService;

    public AgendamentoService(
            AgendamentoRepository agendamentoRepository,
            HorarioDisponivelRepository horarioRepository,
            PacienteRepository pacienteRepository,
            NotificacaoService notificacaoService
    ) {
        this.agendamentoRepository = agendamentoRepository;
        this.horarioRepository = horarioRepository;
        this.pacienteRepository = pacienteRepository;
        this.notificacaoService = notificacaoService;
    }

    @Transactional
    public Agendamento agendar(Long pacienteId, Long horarioId) {
        Paciente paciente = buscarPaciente(pacienteId);
        HorarioDisponivel horario = buscarHorario(horarioId);
        LocalDateTime dataCriacao = LocalDateTime.now();

        validarHorario(horario, dataCriacao);

        Agendamento agendamento = criarAgendamento(
                paciente,
                horario,
                dataCriacao
        );

        horario.reservar();
        horarioRepository.save(horario);

        return agendamentoRepository.save(agendamento);
    }

    public Agendamento buscarPorId(Long agendamentoId) {
        return agendamentoRepository.findDetailsById(agendamentoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Agendamento não encontrado."
                ));
    }

    public List<Agendamento> listarPorProfissional(
            Long profissionalId
    ) {
        return agendamentoRepository
                .findByProfissionalIdOrderByHorarioDataHoraAsc(
                        profissionalId
                );
    }

    public long contarPendentes() {
        return agendamentoRepository.countByStatus(
                StatusAgendamentoEnum.PENDENTE
        );
    }

    @Transactional
    public Agendamento confirmar(Long agendamentoId) {
        Agendamento agendamento = buscarAgendamentoParaAlteracao(
                agendamentoId
        );

        agendamento.confirmar();

        return agendamentoRepository.save(agendamento);
    }

    @Transactional
    public Agendamento cancelar(Long agendamentoId) {
        Agendamento agendamento = buscarAgendamentoParaAlteracao(
                agendamentoId
        );

        if (agendamento.getStatus() == StatusAgendamentoEnum.CANCELADO) {
            return agendamento;
        }

        HorarioDisponivel horario = buscarHorario(
                agendamento.getHorario().getId()
        );

        agendamento.cancelar();
        horario.liberar();

        horarioRepository.save(horario);

        return agendamentoRepository.save(agendamento);
    }

    @Transactional
    public Agendamento concluir(Long agendamentoId) {
        Agendamento agendamento = buscarAgendamentoParaAlteracao(
                agendamentoId
        );

        agendamento.concluir(LocalDateTime.now());

        return agendamentoRepository.save(agendamento);
    }

    private Paciente buscarPaciente(Long pacienteId) {
        return pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paciente não encontrado."
                ));
    }

    private HorarioDisponivel buscarHorario(Long horarioId) {
        return horarioRepository.findByIdForUpdate(horarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Horário não encontrado."
                ));
    }

    private Agendamento buscarAgendamentoParaAlteracao(
            Long agendamentoId
    ) {
        return agendamentoRepository.findByIdForUpdate(agendamentoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Agendamento não encontrado."
                ));
    }

    private void validarHorario(
            HorarioDisponivel horario,
            LocalDateTime dataAtual
    ) {
        if (!horario.isDisponivel()) {
            throw new BusinessException(
                    "O horário não está disponível."
            );
        }

        if (horario.getDataHora() == null
                || !horario.getDataHora().isAfter(dataAtual)) {
            throw new BusinessException(
                    "Não é possível agendar um horário passado."
            );
        }

        if (horario.getProfissional() == null) {
            throw new BusinessException(
                    "O horário não possui um profissional responsável."
            );
        }
    }

    private Agendamento criarAgendamento(
            Paciente paciente,
            HorarioDisponivel horario,
            LocalDateTime dataCriacao
    ) {
        return Agendamento.criar(
                paciente,
                horario,
                dataCriacao
        );
    }


}
