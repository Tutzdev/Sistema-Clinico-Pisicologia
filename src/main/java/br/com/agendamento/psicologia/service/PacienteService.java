package br.com.agendamento.psicologia.service;

import br.com.agendamento.psicologia.entity.Paciente;
import br.com.agendamento.psicologia.exception.BusinessException;
import br.com.agendamento.psicologia.exception.ResourceNotFoundException;
import br.com.agendamento.psicologia.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Transactional
    public Paciente criar(
            String nome,
            String telefone,
            String email
    ) {
        String emailNormalizado = normalizarEmail(email);

        validarEmailDisponivel(emailNormalizado);

        Paciente paciente = new Paciente();
        paciente.setNome(normalizarTexto(nome));
        paciente.setTelefone(normalizarTexto(telefone));
        paciente.setEmail(emailNormalizado);

        return pacienteRepository.save(paciente);
    }

    public Paciente buscarPorId(Long pacienteId) {
        return pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paciente não encontrado."
                ));
    }

    public List<Paciente> listarTodos() {
        return pacienteRepository.findAllByOrderByNomeAsc();
    }

    @Transactional
    public Paciente atualizar(
            Long pacienteId,
            String nome,
            String telefone,
            String email
    ) {
        Paciente paciente = buscarPorId(pacienteId);
        String emailNormalizado = normalizarEmail(email);

        validarEmailDisponivelParaAtualizacao(
                emailNormalizado,
                pacienteId
        );

        paciente.setNome(normalizarTexto(nome));
        paciente.setTelefone(normalizarTexto(telefone));
        paciente.setEmail(emailNormalizado);

        return pacienteRepository.save(paciente);
    }

    @Transactional
    public void excluir(Long pacienteId) {
        Paciente paciente = buscarPorId(pacienteId);

        pacienteRepository.delete(paciente);
    }

    private void validarEmailDisponivel(String email) {
        if (email != null
                && pacienteRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException(
                    "Já existe um paciente cadastrado com este e-mail."
            );
        }
    }

    private void validarEmailDisponivelParaAtualizacao(
            String email,
            Long pacienteId
    ) {
        if (email != null
                && pacienteRepository.existsByEmailIgnoreCaseAndIdNot(
                        email,
                        pacienteId
                )) {
            throw new BusinessException(
                    "Já existe um paciente cadastrado com este e-mail."
            );
        }
    }

    private String normalizarTexto(String valor) {
        return valor.trim();
    }

    private String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }

    public long contarTodos() {
        return pacienteRepository.count();
    }
}