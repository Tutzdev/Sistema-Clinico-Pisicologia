package br.com.agendamento.psicologia.service;

import br.com.agendamento.psicologia.entity.Profissional;
import br.com.agendamento.psicologia.exception.BusinessException;
import br.com.agendamento.psicologia.exception.ResourceNotFoundException;
import br.com.agendamento.psicologia.repository.ProfissionalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class ProfissionalService {

    private final ProfissionalRepository profissionalRepository;

    public ProfissionalService(
            ProfissionalRepository profissionalRepository
    ) {
        this.profissionalRepository = profissionalRepository;
    }

    @Transactional
    public Profissional criar(
            String nome,
            String email,
            String codigoAgenda
    ) {
        String emailNormalizado = normalizarEmail(email);
        String codigoNormalizado = normalizarCodigoAgenda(codigoAgenda);

        validarEmailDisponivel(emailNormalizado);
        validarCodigoAgendaDisponivel(codigoNormalizado);

        Profissional profissional = new Profissional();
        profissional.setNome(normalizarNome(nome));
        profissional.setEmail(emailNormalizado);
        profissional.setCodigoAgenda(codigoNormalizado);

        return profissionalRepository.save(profissional);
    }

    public Profissional buscarPorId(Long profissionalId) {
        return profissionalRepository.findById(profissionalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profissional não encontrado."
                ));
    }

    public Profissional buscarPorEmail(String email) {
        return profissionalRepository
                .findByEmailIgnoreCase(normalizarEmail(email))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profissional não encontrado."
                ));
    }

    public Profissional buscarPorCodigoAgenda(String codigoAgenda) {
        return profissionalRepository
                .findByCodigoAgenda(
                        normalizarCodigoAgenda(codigoAgenda)
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Agenda profissional não encontrada."
                ));
    }

    public List<Profissional> listarTodos() {
        return profissionalRepository.findAllByOrderByNomeAsc();
    }

    @Transactional
    public Profissional atualizar(
            Long profissionalId,
            String nome,
            String email,
            String codigoAgenda
    ) {
        Profissional profissional = buscarPorId(profissionalId);
        String emailNormalizado = normalizarEmail(email);
        String codigoNormalizado = normalizarCodigoAgenda(codigoAgenda);

        validarEmailDisponivelParaAtualizacao(
                emailNormalizado,
                profissionalId
        );

        validarCodigoAgendaDisponivelParaAtualizacao(
                codigoNormalizado,
                profissionalId
        );

        profissional.setNome(normalizarNome(nome));
        profissional.setEmail(emailNormalizado);
        profissional.setCodigoAgenda(codigoNormalizado);

        return profissionalRepository.save(profissional);
    }

    @Transactional
    public void excluir(Long profissionalId) {
        Profissional profissional = buscarPorId(profissionalId);

        profissionalRepository.delete(profissional);
    }

    private void validarEmailDisponivel(String email) {
        if (profissionalRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException(
                    "Já existe um profissional cadastrado com este e-mail."
            );
        }
    }

    private void validarEmailDisponivelParaAtualizacao(
            String email,
            Long profissionalId
    ) {
        if (profissionalRepository.existsByEmailIgnoreCaseAndIdNot(
                email,
                profissionalId
        )) {
            throw new BusinessException(
                    "Já existe um profissional cadastrado com este e-mail."
            );
        }
    }

    private void validarCodigoAgendaDisponivel(String codigoAgenda) {
        if (profissionalRepository.existsByCodigoAgenda(codigoAgenda)) {
            throw new BusinessException(
                    "O código da agenda já está sendo utilizado."
            );
        }
    }

    private void validarCodigoAgendaDisponivelParaAtualizacao(
            String codigoAgenda,
            Long profissionalId
    ) {
        if (profissionalRepository.existsByCodigoAgendaAndIdNot(
                codigoAgenda,
                profissionalId
        )) {
            throw new BusinessException(
                    "O código da agenda já está sendo utilizado."
            );
        }
    }

    private String normalizarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new BusinessException("O nome é obrigatório.");
        }

        return nome.trim();
    }

    private String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BusinessException("O e-mail é obrigatório.");
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizarCodigoAgenda(String codigoAgenda) {
        if (codigoAgenda == null || codigoAgenda.isBlank()) {
            throw new BusinessException(
                    "O código da agenda é obrigatório."
            );
        }

        return codigoAgenda.trim().toLowerCase(Locale.ROOT);
    }

    public long contarTodos() {
        return profissionalRepository.count();
    }
}
