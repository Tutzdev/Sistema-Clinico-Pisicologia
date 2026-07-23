package br.com.agendamento.psicologia.service;

import br.com.agendamento.psicologia.entity.HorarioDisponivel;
import br.com.agendamento.psicologia.repository.HorarioDisponivelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendaService {

    private final HorarioDisponivelRepository horarioRepository;


    public AgendaService(HorarioDisponivelRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

    public List<HorarioDisponivel> listarHorariosDisponiveis() {
        return horarioRepository.findByDisponivelTrue();
    }

    public HorarioDisponivel criarHorario(HorarioDisponivel horario) {
        horario.setDisponivel(true);

        return horarioRepository.save(horario);
    }
}