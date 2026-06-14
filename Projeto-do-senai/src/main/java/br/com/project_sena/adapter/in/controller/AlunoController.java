package br.com.project_sena.adapter.in.controller;

import br.com.project_sena.adapter.in.controller.request.aluno.StudentRequestDTO;
import br.com.project_sena.adapter.in.controller.request.aluno.StudentUpdateDTO;
import br.com.project_sena.adapter.in.controller.response.StudentDetailsDTO;
import br.com.project_sena.adapter.in.controller.response.StudentListAtivosDTO;
import br.com.project_sena.adapter.in.controller.response.StudentListInativosDTO;
import br.com.project_sena.adapter.in.web.mapper.AlunoMapperDTO;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.usecase.AlunoService;
import br.com.project_sena.application.port.in.UsuarioDomainController;
import jakarta.transaction.Transactional;
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
@RequestMapping("/aluno")
public class AlunoController implements UsuarioDomainController<
        StudentRequestDTO,
        StudentListAtivosDTO,
        StudentListInativosDTO,
        StudentUpdateDTO,
        Void,
        StudentDetailsDTO,
        StudentDetailsDTO,
        Long> {

    private final AlunoService alunoService;
    private final AlunoMapperDTO mapper;

    public AlunoController(AlunoService alunoService, AlunoMapperDTO mapper) {
        this.alunoService = alunoService;
        this.mapper = mapper;
     }

     @PostMapping("/cadastrar")
    public ResponseEntity<StudentDetailsDTO> cadastrar(
             @RequestBody @Valid StudentRequestDTO dto,
             UriComponentsBuilder uriBuilder) {
         Aluno domain = mapper.toDomain(dto);
         Aluno alunoCadastrado = alunoService.cadastrar(domain);
         StudentDetailsDTO response = mapper.toDTO(alunoCadastrado);

         URI uri = uriBuilder
                 .path("/aluno/{id}")
                 .buildAndExpand(alunoCadastrado.getId())
                 .toUri();
         return ResponseEntity.created(uri).build();
     }

     @GetMapping("/ativos")
     public ResponseEntity<Page<StudentListAtivosDTO>> listarAtivos(
             @PageableDefault (size = 10, sort = "name", direction = Sort.Direction.DESC)
             Pageable pageable){
        Page<Aluno> aluno = alunoService.listarAtivos(pageable);
        return ResponseEntity.ok(aluno.map(mapper::toListAtivoDTO));
     }

     @GetMapping("/inativos")
    public ResponseEntity<Page<StudentListInativosDTO>> listarInativos(@PageableDefault (size = 10, sort = "name", direction = Sort.Direction.DESC) Pageable pageable){
        Page<Aluno> aluno = alunoService.listarInativos(pageable);
        return ResponseEntity.ok(aluno.map(mapper::toListInvativos));
     }

     @GetMapping("/{id}")
     public ResponseEntity<StudentDetailsDTO> detalhar(@PathVariable Long id){
        Aluno aluno = alunoService.buscar(id);
        StudentDetailsDTO response = mapper.toDTO(aluno);
        return ResponseEntity.ok(response);
     }

     @PutMapping("/atualizar/{id}")
    public ResponseEntity<StudentDetailsDTO> atualizar(@RequestBody @Valid StudentUpdateDTO dto, @PathVariable Long id){
        Aluno domain = mapper.toDomainUpdate(dto);
        StudentDetailsDTO response = mapper.toDTO(alunoService.atualizar(id, domain));
        return ResponseEntity.ok(response);
     }

     @Transactional
     @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id){
        alunoService.excluir(id);
        return ResponseEntity.noContent().build();
     }

     @Transactional
     @PatchMapping("/reativar/{id}")
    public ResponseEntity<StudentDetailsDTO> reativar(@PathVariable Long id){
         Aluno aluno = alunoService.reativar(id);
         StudentDetailsDTO dto = mapper.toDTO(aluno);
         return ResponseEntity.ok(dto);
     }
}
