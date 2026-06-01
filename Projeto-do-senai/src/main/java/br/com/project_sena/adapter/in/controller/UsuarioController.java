package br.com.project_sena.adapter.in.controller;

import br.com.project_sena.adapter.in.controller.request.UserRegisterDTO;
import br.com.project_sena.adapter.in.controller.request.UserUpdateDTO;
import br.com.project_sena.adapter.in.controller.response.UserDetailsDTO;
import br.com.project_sena.adapter.in.controller.response.UserListAtivosDTO;
import br.com.project_sena.adapter.in.controller.response.UserListInativosDTO;
import br.com.project_sena.adapter.in.web.mapper.UsuarioMapperDTO;
import br.com.project_sena.application.core.domain.model.Usuario;
import br.com.project_sena.application.core.usecase.UsuarioService;
import br.com.project_sena.application.port.in.UsuarioDomainModelController;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/usuario")
public class UsuarioController implements UsuarioDomainModelController<
        UserRegisterDTO,
        UserListAtivosDTO,
        UserListInativosDTO,
        UserUpdateDTO,
        Void,
        Void,
        UserDetailsDTO,
        Long> {

    private UsuarioService usuarioService;
    private UsuarioMapperDTO mapper;

    public UsuarioController(UsuarioService usuarioService, UsuarioMapperDTO mapper) {
        this.usuarioService = usuarioService;
        this.mapper = mapper;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<UserDetailsDTO> cadastrar(
            @RequestBody @Valid UserRegisterDTO dto) {
        Usuario domain = mapper.toDomain(dto);
        Usuario salvo = usuarioService.cadastrar(domain);
        UserDetailsDTO response = mapper.toDTO(salvo);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<UserListAtivosDTO>> listarAtivos(@PageableDefault(size = 10, sort = "perfil", page = 0, direction = Sort.Direction.DESC) Pageable pageable) {
        Page <Usuario> usuario = usuarioService.listar(pageable);
        return ResponseEntity.ok(usuario.map(mapper::toList));
    }

    @GetMapping("/inativos")
    public ResponseEntity<Page<UserListInativosDTO>> listarInativos(@PageableDefault(size = 10, sort = "perfil", page = 0, direction = Sort.Direction.DESC) Pageable pageable){
        Page<Usuario> usuario = usuarioService.listarInvativos(pageable);
        return ResponseEntity.ok(usuario.map(mapper::toListInativo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsDTO> detalhar(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscar(id);
        UserDetailsDTO response = mapper.toDTO(usuario);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<UserDetailsDTO> atualizar(
            @RequestBody @Valid UserUpdateDTO dto,
            @PathVariable Long id) {
        Usuario domain = mapper.toDomainUpdate(dto);
        UserDetailsDTO response = mapper.toDTO(usuarioService.atualizar(domain, id));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reativar/{id}")
    public ResponseEntity<Void> reativar(@PathVariable Long id){
        usuarioService.reativar(id);
        return ResponseEntity.noContent().build();
    }
}
