package br.com.agendamento.psicologia.repository;

import br.com.agendamento.psicologia.entity.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByProfissionalId(Long profissionalId);

}