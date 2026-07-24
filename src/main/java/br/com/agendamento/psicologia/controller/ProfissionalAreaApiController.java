package br.com.agendamento.psicologia.controller;

import br.com.agendamento.psicologia.dto.AgendamentoResponseDTO;
import br.com.agendamento.psicologia.dto.HorarioDisponivelDTO;
import br.com.agendamento.psicologia.dto.NovoHorarioRequestDTO;
import br.com.agendamento.psicologia.entity.Profissional;
import br.com.agendamento.psicologia.service.AgendaService;
import br.com.agendamento.psicologia.service.AgendamentoService;
import br.com.agendamento.psicologia.service.UsuarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Validated
@RestController
@RequestMapping("/api/profissional/me")
public class ProfissionalAreaApiController {

    private final UsuarioService usuarioService;
    private final AgendamentoService agendamentoService;
    private final AgendaService agendaService;

    public ProfissionalAreaApiController(
            UsuarioService usuarioService,
            AgendamentoService agendamentoService,
            AgendaService agendaService
    ) {
        this.usuarioService = usuarioService;
        this.agendamentoService = agendamentoService;
        this.agendaService = agendaService;
    }

    @GetMapping("/agendamentos")
    public List<AgendamentoResponseDTO> listarAgendamentos(
            Authentication authentication
    ) {
        Profissional profissional = profissional(authentication);

        return agendamentoService
                .listarPorProfissional(profissional.getId())
                .stream()
                .map(AgendamentoResponseDTO::from)
                .toList();
    }

    @GetMapping("/horarios")
    public List<HorarioDisponivelDTO> listarHorarios(
            Authentication authentication
    ) {
        Profissional profissional = profissional(authentication);

        return agendaService
                .listarHorariosDisponiveisPorProfissional(
                        profissional.getId()
                )
                .stream()
                .map(HorarioDisponivelDTO::from)
                .toList();
    }

    @PostMapping("/horarios")
    @ResponseStatus(CREATED)
    public HorarioDisponivelDTO criarHorario(
            @Valid @RequestBody NovoHorarioRequestDTO request,
            Authentication authentication
    ) {
        Profissional profissional = profissional(authentication);

        return HorarioDisponivelDTO.from(
                agendaService.criarHorario(
                        profissional.getId(),
                        request.dataHora()
                )
        );
    }

    @DeleteMapping("/horarios/{horarioId}")
    @ResponseStatus(NO_CONTENT)
    public void excluirHorario(
            @PathVariable @Positive Long horarioId,
            Authentication authentication
    ) {
        agendaService.excluirHorarioDoProfissional(
                horarioId,
                profissional(authentication).getId()
        );
    }

    @PatchMapping("/agendamentos/{agendamentoId}/confirmacao")
    public AgendamentoResponseDTO confirmar(
            @PathVariable @Positive Long agendamentoId,
            Authentication authentication
    ) {
        return AgendamentoResponseDTO.from(
                agendamentoService.confirmarDoProfissional(
                        agendamentoId,
                        profissional(authentication).getId()
                )
        );
    }

    @PatchMapping("/agendamentos/{agendamentoId}/cancelamento")
    public AgendamentoResponseDTO cancelar(
            @PathVariable @Positive Long agendamentoId,
            Authentication authentication
    ) {
        return AgendamentoResponseDTO.from(
                agendamentoService.cancelarDoProfissional(
                        agendamentoId,
                        profissional(authentication).getId()
                )
        );
    }

    @PatchMapping("/agendamentos/{agendamentoId}/conclusao")
    public AgendamentoResponseDTO concluir(
            @PathVariable @Positive Long agendamentoId,
            Authentication authentication
    ) {
        return AgendamentoResponseDTO.from(
                agendamentoService.concluirDoProfissional(
                        agendamentoId,
                        profissional(authentication).getId()
                )
        );
    }

    private Profissional profissional(
            Authentication authentication
    ) {
        return usuarioService.buscarProfissionalDoUsuario(
                authentication.getName()
        );
    }
}
