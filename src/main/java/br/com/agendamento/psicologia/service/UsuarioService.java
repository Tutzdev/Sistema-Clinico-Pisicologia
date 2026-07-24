package br.com.agendamento.psicologia.service;

import br.com.agendamento.psicologia.entity.Profissional;
import br.com.agendamento.psicologia.entity.Usuario;
import br.com.agendamento.psicologia.enums.RoleEnum;
import br.com.agendamento.psicologia.exception.BusinessException;
import br.com.agendamento.psicologia.exception.ResourceNotFoundException;
import br.com.agendamento.psicologia.repository.ProfissionalRepository;
import br.com.agendamento.psicologia.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private static final int TAMANHO_MINIMO_SENHA = 8;
    private static final int TAMANHO_MAXIMO_SENHA = 72;

    private final UsuarioRepository usuarioRepository;
    private final ProfissionalRepository profissionalRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            ProfissionalRepository profissionalRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.profissionalRepository = profissionalRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario criar(
            String email,
            String senha,
            RoleEnum role,
            Long profissionalId
    ) {
        validarRole(role);

        return switch (role) {
            case ADMIN -> criarAdministrador(
                    email,
                    senha,
                    profissionalId
            );

            case PROFISSIONAL -> criarUsuarioProfissional(
                    email,
                    senha,
                    profissionalId
            );
        };
    }

    public Usuario buscarPorId(Long usuarioId) {
        return usuarioRepository.findDetailsById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário não encontrado."
                ));
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository
                .findByEmailIgnoreCase(normalizarEmail(email))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário não encontrado."
                ));
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAllByOrderByEmailAsc();
    }

    public Profissional buscarProfissionalDoUsuario(String email) {
        Usuario usuario = buscarPorEmail(email);

        if (usuario.getRole() != RoleEnum.PROFISSIONAL
                || usuario.getProfissional() == null) {
            throw new BusinessException(
                    "O usuário não possui um profissional vinculado."
            );
        }

        return usuario.getProfissional();
    }

    @Transactional
    public Usuario criarAdministradorInicial(
            String email,
            String senha
    ) {
        return criarUsuario(
                email,
                senha,
                RoleEnum.ADMIN,
                null
        );
    }

    @Transactional
    public void alterarSenha(
            Long usuarioId,
            String novaSenha
    ) {
        validarSenha(novaSenha);

        Usuario usuario = buscarPorId(usuarioId);
        usuario.alterarSenha(
                passwordEncoder.encode(novaSenha)
        );

        usuarioRepository.save(usuario);
    }

    @Transactional
    public void ativar(Long usuarioId) {
        Usuario usuario = buscarPorId(usuarioId);
        usuario.ativar();

        usuarioRepository.save(usuario);
    }

    @Transactional
    public void desativar(Long usuarioId) {
        Usuario usuario = buscarPorId(usuarioId);

        validarUltimoAdministrador(usuario);

        usuario.desativar();

        usuarioRepository.save(usuario);
    }

    private Usuario criarAdministrador(
            String email,
            String senha,
            Long profissionalId
    ) {
        if (profissionalId != null) {
            throw new BusinessException(
                    "Um administrador não deve possuir profissional vinculado."
            );
        }

        return criarUsuario(
                email,
                senha,
                RoleEnum.ADMIN,
                null
        );
    }

    private Usuario criarUsuarioProfissional(
            String email,
            String senha,
            Long profissionalId
    ) {
        Profissional profissional = buscarProfissionalObrigatorio(
                profissionalId
        );

        validarProfissionalDisponivel(profissionalId);

        return criarUsuario(
                email,
                senha,
                RoleEnum.PROFISSIONAL,
                profissional
        );
    }

    private Usuario criarUsuario(
            String email,
            String senha,
            RoleEnum role,
            Profissional profissional
    ) {
        String emailNormalizado = normalizarEmail(email);

        validarEmailDisponivel(emailNormalizado);
        validarSenha(senha);

        Usuario usuario = Usuario.criar(
                emailNormalizado,
                passwordEncoder.encode(senha),
                role,
                profissional
        );

        return usuarioRepository.save(usuario);
    }

    private Profissional buscarProfissionalObrigatorio(
            Long profissionalId
    ) {
        if (profissionalId == null) {
            throw new BusinessException(
                    "O profissional é obrigatório para este perfil."
            );
        }

        return profissionalRepository.findById(profissionalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profissional não encontrado."
                ));
    }

    private void validarRole(RoleEnum role) {
        if (role == null) {
            throw new BusinessException(
                    "O perfil de acesso é obrigatório."
            );
        }
    }

    private void validarEmailDisponivel(String email) {
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException(
                    "Já existe um usuário cadastrado com este e-mail."
            );
        }
    }

    private void validarProfissionalDisponivel(Long profissionalId) {
        if (usuarioRepository.existsByProfissionalId(profissionalId)) {
            throw new BusinessException(
                    "O profissional já possui um usuário vinculado."
            );
        }
    }

    private void validarSenha(String senha) {
        if (senha == null
                || senha.length() < TAMANHO_MINIMO_SENHA
                || senha.length() > TAMANHO_MAXIMO_SENHA) {
            throw new BusinessException(
                    "A senha deve possuir entre 8 e 72 caracteres."
            );
        }
    }

    private void validarUltimoAdministrador(Usuario usuario) {
        if (usuario.getRole() == RoleEnum.ADMIN
                && usuario.isAtivo()
                && usuarioRepository.countByRoleAndAtivoTrue(
                        RoleEnum.ADMIN
                ) <= 1) {
            throw new BusinessException(
                    "O último administrador ativo não pode ser desativado."
            );
        }
    }

    private String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BusinessException(
                    "O e-mail é obrigatório."
            );
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }
}
