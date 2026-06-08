package br.com.project_sena.adapter.in.controller;

import br.com.project_sena.adapter.in.controller.request.turma.ClassRegisterDTO;
import br.com.project_sena.adapter.in.controller.request.turma.ClassUpdateDTO;
import br.com.project_sena.adapter.in.controller.response.ClassDetailsDTO;
import br.com.project_sena.adapter.in.controller.response.ClassListAtivoDTO;
import br.com.project_sena.adapter.in.controller.response.ClassListInativosDTO;
import br.com.project_sena.adapter.in.controller.response.VincularDetailsDTO;
import br.com.project_sena.adapter.in.web.mapper.TurmaMapperDTO;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.core.usecase.TurmaService;
import br.com.project_sena.application.port.in.UsuarioDomainController;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/turmas")
public class TurmaController implements UsuarioDomainController<
        ClassRegisterDTO,
        ClassListAtivoDTO,
        ClassListInativosDTO,
        ClassUpdateDTO,
        Void,
        ClassDetailsDTO,
        ClassDetailsDTO,
        Long
        > {

    private final TurmaService service;
    private final TurmaMapperDTO mapper;

    public TurmaController(TurmaService service, TurmaMapperDTO mapper){
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<ClassDetailsDTO> cadastrar(
            @RequestBody @Valid ClassRegisterDTO dto,
            UriComponentsBuilder builder){
        Turma domain = mapper.toDomain(dto);
        Turma turmaCadastrada = service.cadastrar(domain);
        ClassDetailsDTO response = mapper.toDetailsDTO(turmaCadastrada);
        URI uri = builder
                .path("/turma/{id}")
                .buildAndExpand(turmaCadastrada.getId())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @PostMapping("/vincular/{id}/{id}")
    public ResponseEntity<VincularDetailsDTO> cadastrarVinculo(){
        return null;
    }

    @GetMapping("/listar/inativo")
    public ResponseEntity<Page<ClassListAtivoDTO>> listarAtivos(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.DESC)
            Pageable pageable){
       Page<Turma> turma = service.listarTurmasAtivas(pageable);
       return ResponseEntity.ok(turma.map(mapper::toListAtivosDTO));
    }

    @GetMapping("/listar/ativo")
    public ResponseEntity<Page<ClassListInativosDTO>> listarInativos(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.DESC)
            Pageable pageable){
        Page<Turma> turma = service.listarTurmasCanceladas(pageable);
        return ResponseEntity.ok(turma.map(mapper::toListInativos));
    }

    @GetMapping("/detalhar/{id}")
    public ResponseEntity<ClassDetailsDTO> detalhar(@PathVariable Long id){
        Turma turma = service.buscar(id);
        ClassDetailsDTO response = mapper.toDetailsDTO(turma);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<ClassDetailsDTO> atualizar(
            @RequestBody @Valid ClassUpdateDTO dto, @PathVariable Long id){
        Turma domain = mapper.toUpdateDTO(dto);
        ClassDetailsDTO response = mapper.toDetailsDTO(service.atualizarTurma(domain, id));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id){
         service.deletar(id);
         return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reativar/{id}")
    public ResponseEntity<ClassDetailsDTO> reativar(@PathVariable Long id){
        Turma turma = service.reativar(id);
        ClassDetailsDTO dto = mapper.toDetailsDTO(turma);
        return ResponseEntity.ok(dto);
    }
}
