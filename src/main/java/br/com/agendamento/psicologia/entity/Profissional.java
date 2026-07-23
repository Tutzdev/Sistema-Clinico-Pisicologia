package br.com.agendamento.psicologia.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "profissionais")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Profissional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true)
    private String email;

    private String senha;

    @Column(unique = true)
    private String codigoAgenda;
}