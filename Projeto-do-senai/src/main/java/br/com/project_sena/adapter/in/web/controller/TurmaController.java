package br.com.project_sena.adapter.in.web.controller;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.project_sena.adapter.in.web.dto.request.EnrollmentRequest;
import br.com.project_sena.adapter.in.web.dto.request.SchoolClassRegisterRequest;
import br.com.project_sena.adapter.in.web.dto.request.SchoolClassUpdateRequest;
import br.com.project_sena.adapter.in.web.dto.response.PageResponse;
import br.com.project_sena.adapter.in.web.dto.response.SchoolClassResponse;
import br.com.project_sena.adapter.in.web.dto.response.StudentResponse;
import br.com.project_sena.adapter.in.web.mapper.AlunoWebMapper;
import br.com.project_sena.adapter.in.web.mapper.PaginacaoWeb;
import br.com.project_sena.adapter.in.web.mapper.TurmaWebMapper;
import br.com.project_sena.application.core.domain.enums.SemestreEnum;
import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import br.com.project_sena.application.core.domain.enums.TurmaTurnoEnum;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.port.in.TurmaUseCase;
import br.com.project_sena.application.port.in.command.VincularAlunoCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/schoolclasses")
@Tag(name = "Turmas", description = "Gerenciamento de turmas e matriculas")
public class TurmaController {

    private final TurmaUseCase turmaUseCase;
    private final TurmaWebMapper mapper;
    private final AlunoWebMapper alunoMapper;

    public TurmaController(TurmaUseCase turmaUseCase, TurmaWebMapper mapper, AlunoWebMapper alunoMapper) {
        this.turmaUseCase = turmaUseCase;
        this.mapper = mapper;
        this.alunoMapper = alunoMapper;
    }

    @PostMapping
    @Operation(summary = "Cadastrar turma")
    public ResponseEntity<SchoolClassResponse> cadastrar(
            @RequestBody @Valid SchoolClassRegisterRequest request,
            UriComponentsBuilder uriBuilder) {
        Turma turma = turmaUseCase.cadastrar(mapper.toCommand(request));
        URI uri = uriBuilder.path("/schoolclasses/{id}").buildAndExpand(turma.getId()).toUri();
        return ResponseEntity.created(uri).body(mapper.toResponse(turma));
    }

    /**
     * Turmas ativas.
     *
     * <p>Na versao anterior o caminho e o conteudo estavam trocados:
     * {@code /turmas/listar/inativo} devolvia as ativas e {@code /listar/ativo} as
     * canceladas.</p>
     */
    @GetMapping
    @Operation(summary = "Listar turmas ativas")
    public ResponseEntity<PageResponse<SchoolClassResponse>> listarAtivas(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction) {
        return ResponseEntity.ok(PageResponse.de(
                turmaUseCase.listar(TurmaEnum.ATIVA, PaginacaoWeb.de(page, size, sort, direction)),
                mapper::toResponse));
    }

    @GetMapping("/canceled")
    @Operation(summary = "Listar turmas canceladas")
    public ResponseEntity<PageResponse<SchoolClassResponse>> listarCanceladas(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction) {
        return ResponseEntity.ok(PageResponse.de(
                turmaUseCase.listar(TurmaEnum.CANCELADA, PaginacaoWeb.de(page, size, sort, direction)),
                mapper::toResponse));
    }

    @GetMapping("/shifts")
    @Operation(summary = "Turnos disponiveis")
    public ResponseEntity<List<String>> turnos() {
        return ResponseEntity.ok(Arrays.stream(TurmaTurnoEnum.values()).map(Enum::name).toList());
    }

    @GetMapping("/semesters")
    @Operation(summary = "Semestres disponiveis")
    public ResponseEntity<List<String>> semestres() {
        return ResponseEntity.ok(Arrays.stream(SemestreEnum.values()).map(Enum::name).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhar turma")
    public ResponseEntity<SchoolClassResponse> detalhar(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(turmaUseCase.buscar(id)));
    }

    @PutMapping
    @Operation(summary = "Atualizar turma")
    public ResponseEntity<SchoolClassResponse> atualizar(
            @RequestBody @Valid SchoolClassUpdateRequest request) {
        return ResponseEntity.ok(mapper.toResponse(turmaUseCase.atualizar(mapper.toCommand(request))));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar turma")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        turmaUseCase.cancelar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivate")
    @Operation(summary = "Reativar turma")
    public ResponseEntity<SchoolClassResponse> reativar(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(turmaUseCase.reativar(id)));
    }

    /**
     * Matricula de aluno.
     *
     * <p>A versao anterior tinha o endpoint {@code /vincular/aluno/{id}} declarado sem
     * parametros e com corpo {@code return null}, entao a matricula nunca acontecia.</p>
     */
    @PostMapping("/{id}/students")
    @Operation(summary = "Matricular aluno na turma")
    public ResponseEntity<List<StudentResponse>> matricular(@PathVariable Long id,
                                                            @RequestBody @Valid EnrollmentRequest request) {
        turmaUseCase.vincularAluno(new VincularAlunoCommand(request.studentId(), id));
        return ResponseEntity.ok(alunosDaTurma(id));
    }

    @GetMapping("/{id}/students")
    @Operation(summary = "Listar alunos da turma")
    public ResponseEntity<List<StudentResponse>> listarAlunos(@PathVariable Long id) {
        return ResponseEntity.ok(alunosDaTurma(id));
    }

    private List<StudentResponse> alunosDaTurma(Long turmaId) {
        return turmaUseCase.listarAlunos(turmaId).stream().map(alunoMapper::toResponse).toList();
    }
}
