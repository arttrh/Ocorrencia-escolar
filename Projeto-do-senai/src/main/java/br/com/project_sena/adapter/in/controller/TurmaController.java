package br.com.project_sena.adapter.in.controller;
import br.com.project_sena.adapter.in.controller.request.turma.ClassRegisterDTO;
import br.com.project_sena.adapter.in.controller.request.turma.ClassUpdateDTO;
import br.com.project_sena.adapter.in.controller.request.turma.VincularStudentAndClassDTO;
import br.com.project_sena.adapter.in.controller.response.ClassDetailsDTO;
import br.com.project_sena.adapter.in.controller.response.ClassListAtivoDTO;
import br.com.project_sena.adapter.in.controller.response.ClassListInativosDTO;
import br.com.project_sena.adapter.in.controller.response.VincularDetailsDTO;
import br.com.project_sena.adapter.in.web.mapper.TurmaMapperDTO;
import br.com.project_sena.application.core.domain.enums.TurmaTurnoEnum;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.core.usecase.AlunoService;
import br.com.project_sena.application.core.usecase.TurmaService;
import br.com.project_sena.application.port.in.UsuarioDomainController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/turmas")
@Tag(name = "Turmas", description = "Gerenciamento de turmas")
public class TurmaController implements UsuarioDomainController<ClassRegisterDTO, ClassListAtivoDTO,
    ClassListInativosDTO, ClassUpdateDTO, Void, ClassDetailsDTO, ClassDetailsDTO, Long> {
    private final TurmaService service;
    private final TurmaMapperDTO mapper;
    private final AlunoService alunoService;

    public TurmaController(TurmaService service, TurmaMapperDTO mapper, AlunoService alunoService) {
        this.service = service;
        this.mapper = mapper;
        this.alunoService = alunoService;
    }

    @GetMapping("/turnos")
    public TurmaTurnoEnum[] turnos() {
        return TurmaTurnoEnum.values();
    }
    @GetMapping("/semestres")
    public List<String> semestres() {
        return List.of("PRIMEIRO", "SEGUNDO");
    }

    @Operation(summary = "Cadastrar turma", description = "Cria uma nova turma")
    @PostMapping("/cadastrar")
    public ResponseEntity<ClassDetailsDTO> cadastrar(
        @RequestBody @Valid ClassRegisterDTO dto, UriComponentsBuilder builder) {
        Turma domain = mapper.toDomain(dto);
        Turma turmaCadastrada = service.cadastrar(domain);
        ClassDetailsDTO response = mapper.toDetailsDTO(turmaCadastrada);
        URI uri = builder.path("/turmas/detalhar/{id}").buildAndExpand(turmaCadastrada.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @Operation(summary = "Listar turmas ativas")
    @PostMapping("/vincular/aluno/{id}")
    public ResponseEntity<Void> cadastrarVinculo(
        @PathVariable Long id, @RequestBody @Valid VincularStudentAndClassDTO dto) {
        service.vincularAlunoTurma(service.buscar(id), alunoService.buscar(dto.studentId()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Excluir turma", description = "Exclusão lógica, muda status para CANCELADA")
    @GetMapping("/listar/ativo")
    public ResponseEntity<Page<ClassListAtivoDTO>> listarAtivos(
        @PageableDefault(size = 10, sort = "className", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Turma> turma = service.listarTurmasAtivas(pageable);
        return ResponseEntity.ok(turma.map(mapper::toListAtivosDTO));
    }

    @GetMapping("/listar/inativo")
    public ResponseEntity<Page<ClassListInativosDTO>> listarInativos(
        @PageableDefault(size = 10, sort = "className", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Turma> turma = service.listarTurmasCanceladas(pageable);
        return ResponseEntity.ok(turma.map(mapper::toListInativos));
    }

    @GetMapping("/detalhar/{id}")
    public ResponseEntity<ClassDetailsDTO> detalhar(@PathVariable Long id) {
        Turma turma = service.buscar(id);
        ClassDetailsDTO response = mapper.toDetailsDTO(turma);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<ClassDetailsDTO> atualizar(
        @RequestBody @Valid ClassUpdateDTO dto, @PathVariable Long id) {
        Turma domain = mapper.toUpdateDTO(dto);
        ClassDetailsDTO response = mapper.toDetailsDTO(service.atualizarTurma(domain, id));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reativar/{id}")
    public ResponseEntity<ClassDetailsDTO> reativar(@PathVariable Long id) {
        Turma turma = service.reativar(id);
        ClassDetailsDTO dto = mapper.toDetailsDTO(turma);
        return ResponseEntity.ok(dto);
    }
}
