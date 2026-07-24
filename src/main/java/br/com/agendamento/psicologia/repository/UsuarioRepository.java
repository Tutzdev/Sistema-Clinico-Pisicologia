package br.com.agendamento.psicologia.repository;

import br.com.agendamento.psicologia.entity.Usuario;
import br.com.agendamento.psicologia.enums.RoleEnum;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository
        extends JpaRepository<Usuario, Long> {

    @EntityGraph(attributePaths = "profissional")
    List<Usuario> findAllByOrderByEmailAsc();

    Optional<Usuario> findByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "profissional")
    @Query("""
            SELECT usuario
            FROM Usuario usuario
            WHERE usuario.id = :usuarioId
            """)
    Optional<Usuario> findDetailsById(
            @Param("usuarioId") Long usuarioId
    );

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByProfissionalId(Long profissionalId);

    boolean existsByRole(RoleEnum role);

    long countByRoleAndAtivoTrue(RoleEnum role);
}