package br.com.agendamento.psicologia.service;

import br.com.agendamento.psicologia.entity.HorarioDisponivel;
import br.com.agendamento.psicologia.entity.Profissional;
import br.com.agendamento.psicologia.exception.BusinessException;
import br.com.agendamento.psicologia.exception.ResourceNotFoundException;
import br.com.agendamento.psicologia.repository.HorarioDisponivelRepository;
import br.com.agendamento.psicologia.repository.ProfissionalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AgendaService {

    private final HorarioDisponivelRepository horarioRepository;
    private final ProfissionalRepository profissionalRepository;

    public AgendaService(
            HorarioDisponivelRepository horarioRepository,
            ProfissionalRepository profissionalRepository
    ) {
        this.horarioRepository = horarioRepository;
        this.profissionalRepository = profissionalRepository;
    }

    public List<HorarioDisponivel> listarHorariosDisponiveis() {
        return horarioRepository
                .findByDisponivelTrueAndDataHoraAfterOrderByDataHoraAsc(
                        LocalDateTime.now()
                );
    }

    public List<HorarioDisponivel>
            listarHorariosDisponiveisPorProfissional(
                    Long profissionalId
            ) {
        return horarioRepository
                .findByProfissionalIdAndDisponivelTrueAndDataHoraAfterOrderByDataHoraAsc(
                        profissionalId,
                        LocalDateTime.now()
                );
    }

    public HorarioDisponivel buscarPorId(Long horarioId) {
        return horarioRepository.findById(horarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Horário não encontrado."
                ));
    }

    @Transactional
    public HorarioDisponivel criarHorario(
            Long profissionalId,
            LocalDateTime dataHora
    ) {
        Profissional profissional = buscarProfissional(
                profissionalId
        );

        validarDataFutura(dataHora);
        validarHorarioDuplicado(profissionalId, dataHora);

        HorarioDisponivel horario = HorarioDisponivel.criar(
                dataHora,
                profissional
        );

        return horarioRepository.save(horario);
    }

    @Transactional
    public void excluirHorario(Long horarioId) {
        HorarioDisponivel horario = buscarHorarioParaAlteracao(
                horarioId
        );

        if (!horario.isDisponivel()) {
            throw new BusinessException(
                    "Um horário reservado não pode ser excluído."
            );
        }

        horarioRepository.delete(horario);
    }

    @Transactional
    public void excluirHorarioDoProfissional(
            Long horarioId,
            Long profissionalId
    ) {
        HorarioDisponivel horario = buscarHorarioParaAlteracao(
                horarioId
        );

        if (!horario.getProfissional().getId().equals(profissionalId)) {
            throw new BusinessException(
                    "Este horário não pertence à sua agenda."
            );
        }

        if (!horario.isDisponivel()) {
            throw new BusinessException(
                    "Um horário reservado não pode ser excluído."
            );
        }

        horarioRepository.delete(horario);
    }

    private HorarioDisponivel buscarHorarioParaAlteracao(
            Long horarioId
    ) {
        return horarioRepository.findByIdForUpdate(horarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Horário não encontrado."
                ));
    }

    private Profissional buscarProfissional(Long profissionalId) {
        return profissionalRepository.findById(profissionalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profissional não encontrado."
                ));
    }

    private void validarDataFutura(LocalDateTime dataHora) {
        if (dataHora == null
                || !dataHora.isAfter(LocalDateTime.now())) {
            throw new BusinessException(
                    "O horário deve estar no futuro."
            );
        }
    }

    private void validarHorarioDuplicado(
            Long profissionalId,
            LocalDateTime dataHora
    ) {
        if (horarioRepository.existsByProfissionalIdAndDataHora(
                profissionalId,
                dataHora
        )) {
            throw new BusinessException(
                    "O profissional já possui esse horário cadastrado."
            );
        }
    }

    public long contarHorariosDisponiveis() {
        return horarioRepository
                .countByDisponivelTrueAndDataHoraAfter(
                        LocalDateTime.now()
                );
    }
}
