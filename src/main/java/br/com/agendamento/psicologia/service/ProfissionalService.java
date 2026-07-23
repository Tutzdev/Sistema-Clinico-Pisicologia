package br.com.agendamento.psicologia.service;

import br.com.agendamento.psicologia.entity.Profissional;
import br.com.agendamento.psicologia.repository.ProfissionalRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfissionalService {

    private final ProfissionalRepository profissionalRepository

    public ProfssionalService(ProfissionalRepository profissionalRepository) {
        this.profissionalRepository = profssionalService;
    }

    public Optional<Profissional> buscarPorEmail(String email) {
        return profissionalRepository.findByEmail(email);
    }

    public Optional<Profissional> buscarPorCodigoAgenda(String codigoAgenda) {
        return profissionalRepository.findByCodigoAgenda(codigoAgenda);
    }

    public Profissional salvar(Profissional profissional) {
        return profissionalRepository.save(profissional);
    }
}