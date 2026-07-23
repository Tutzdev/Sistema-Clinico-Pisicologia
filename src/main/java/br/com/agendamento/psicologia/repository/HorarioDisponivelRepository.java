package br.com.agendamento.psicologia.repository;

import br.com.agendamento.psicologia.entity.HorarioDisponivel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HorarioDisponivelRepository extends JpaRepository<HorarioDisponivel, Long> {

    List<HorarioDisponivel> findByDisponivelTrue();

}