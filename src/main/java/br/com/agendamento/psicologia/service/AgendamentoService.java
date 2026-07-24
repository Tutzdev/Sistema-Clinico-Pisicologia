package br.com.agendamento.psicologia.service;

import br.com.agendamento.psicologia.entity.HorarioDisponivel;
import br.com.agendamento.psicologia.entity.Paciente;
import br.com.agendamento.psicologia.exception.ResourceNotFoundException;
import br.com.agendamento.psicologia.repository.AgendamentoRepository;
import br.com.agendamento.psicologia.repository.HorarioDisponivelRepository;
import br.com.agendamento.psicologia.repository.PacienteRepository;
import org.springframework.stereotype.Service;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final HorarioDisponivelRepository horarioRepository;
    private final PacienteRepository pacienteRepository;

    public AgendamentoService(
            AgendamentoRepository agendamentoRepository,
            HorarioDisponivelRepository horarioRepository,
            PacienteRepository pacienteRepository
    ) {
        this.agendamentoRepository = agendamentoRepository;
        this.horarioRepository = horarioRepository;
        this.pacienteRepository = pacienteRepository;
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

        horario.setDisponivel(false)
        horarioRepository.save(horario);

        return agendamentoRepository.save(agendamento);
    }

    public Agendamento buscarPorId(Long agendamentoId) {
        return agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado."));
    }

    private Paciente buscarPaciente(Long pacienteId) {
        return pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paciente não encontrado."
                ));
    }

    private HorarioDisponivel buscarHorario(Long horarioId) {
        return horarioRepository.findById(horarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Horário não encontrado."
                ));
    }

    private void validarHorario(
            HorarioDisponivel horario,
            LocalDateTime dataAtual
    ) {
        if (!horario.isDisponivel()) {
            throw new BusinessException("O horário não está disponível.");
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
        Agendamento agendamento = new Agendamento();
        agendamento.setDataCriacao(dataCriacao);
        agentamento.setConfirmado(false);
        agendamento.setPaciente(paciente);
        agendamento.setHorario(horario);
        agendamento.setProfssional(horario.getProfissional());

        return agendamento;
    }
}