package br.com.project_sena.adapter.in.web.controller;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

import br.com.project_sena.adapter.in.web.dto.request.UserPasswordRequest;
import br.com.project_sena.adapter.in.web.dto.request.UserRegisterRequest;
import br.com.project_sena.adapter.in.web.dto.request.UserRoleRequest;
import br.com.project_sena.adapter.in.web.dto.request.UserUpdateRequest;
import br.com.project_sena.adapter.in.web.dto.response.PageResponse;
import br.com.project_sena.adapter.in.web.dto.response.UserResponse;
import br.com.project_sena.adapter.in.web.mapper.PaginacaoWeb;
import br.com.project_sena.adapter.in.web.mapper.UsuarioWebMapper;
import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import br.com.project_sena.application.core.domain.enums.UsuarioEnum;
import br.com.project_sena.application.core.domain.exception.AuthorizationException;
import br.com.project_sena.application.core.domain.exception.RegraDeNegocioException;
import br.com.project_sena.application.core.domain.model.Usuario;
import br.com.project_sena.application.port.in.UsuarioUseCase;
import br.com.project_sena.application.port.in.command.AlterarPerfilCommand;
import br.com.project_sena.application.port.in.command.AlterarSenhaCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@Tag(name = "Usuarios", description = "Gerenciamento de usuarios do sistema")
public class UsuarioController {

    /**
     * Extrai o {@link Usuario} de dominio de dentro do principal do Spring Security.
     *
     * <p>Sem isso o controller precisaria importar {@code UsuarioPrincipal}, que vive no
     * adaptador de saida — um adaptador de entrada acessando um de saida, exatamente o que
     * o scanner de arquitetura reprova.</p>
     */
    private static final String USUARIO_AUTENTICADO = "usuario";

    private final UsuarioUseCase usuarioUseCase;
    private final UsuarioWebMapper mapper;

    public UsuarioController(UsuarioUseCase usuarioUseCase, UsuarioWebMapper mapper) {
        this.usuarioUseCase = usuarioUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Cadastrar usuario")
    public ResponseEntity<UserResponse> cadastrar(@RequestBody @Valid UserRegisterRequest request,
                                                  UriComponentsBuilder uriBuilder) {
        Usuario usuario = usuarioUseCase.cadastrar(mapper.toCommand(request));
        URI uri = uriBuilder.path("/users/{id}").buildAndExpand(usuario.getId()).toUri();
        return ResponseEntity.created(uri).body(mapper.toResponse(usuario));
    }

    @GetMapping
    @Operation(summary = "Listar usuarios ativos")
    public ResponseEntity<PageResponse<UserResponse>> listarAtivos(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction) {
        return ResponseEntity.ok(PageResponse.de(
                usuarioUseCase.listar(UsuarioEnum.ATIVO, PaginacaoWeb.de(page, size, sort, direction)),
                mapper::toResponse));
    }

    @GetMapping("/inactive")
    @Operation(summary = "Listar usuarios inativos")
    public ResponseEntity<PageResponse<UserResponse>> listarInativos(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction) {
        return ResponseEntity.ok(PageResponse.de(
                usuarioUseCase.listar(UsuarioEnum.INATIVO, PaginacaoWeb.de(page, size, sort, direction)),
                mapper::toResponse));
    }

    /**
     * Perfis disponiveis, como lista de nomes.
     *
     * <p>Devolve strings simples porque e' o que o {@code <select id="role">} do front
     * consome ({@code option.value = role}).</p>
     */
    @GetMapping("/roles")
    @Operation(summary = "Perfis disponiveis")
    public ResponseEntity<List<String>> perfis() {
        return ResponseEntity.ok(Arrays.stream(PerfilEnum.values()).map(Enum::name).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhar usuario")
    public ResponseEntity<UserResponse> detalhar(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(usuarioUseCase.buscar(id)));
    }

    @PutMapping
    @Operation(summary = "Atualizar usuario")
    public ResponseEntity<UserResponse> atualizar(@RequestBody @Valid UserUpdateRequest request) {
        return ResponseEntity.ok(mapper.toResponse(usuarioUseCase.atualizar(mapper.toCommand(request))));
    }

    @PatchMapping
    @Operation(summary = "Alterar perfil de acesso")
    public ResponseEntity<UserResponse> alterarPerfil(@RequestBody @Valid UserRoleRequest request,
                                                      @AuthenticationPrincipal(expression = USUARIO_AUTENTICADO) Usuario autenticado) {
        // Rebaixar a si mesmo tiraria o ultimo administrador do ar sem volta pela API.
        if (autenticado != null && request.id().equals(autenticado.getId())
                && request.role() != autenticado.getPerfil()) {
            throw new RegraDeNegocioException("Voce nao pode alterar o proprio perfil de acesso");
        }
        return ResponseEntity.ok(
                mapper.toResponse(usuarioUseCase.alterarPerfil(
                        new AlterarPerfilCommand(request.id(), request.role()))));
    }

    /**
     * Troca de senha.
     *
     * <p>Um usuario so' pode trocar a propria senha; administradores podem trocar a de
     * qualquer um. Sem essa checagem qualquer conta autenticada poderia sobrescrever a
     * senha de outra apenas mudando o {@code id} do corpo da requisicao.</p>
     */
    @PatchMapping("/password")
    @Operation(summary = "Alterar senha")
    public ResponseEntity<Void> alterarSenha(@RequestBody @Valid UserPasswordRequest request,
                                             @AuthenticationPrincipal(expression = USUARIO_AUTENTICADO) Usuario autenticado) {
        boolean ehAdmin = autenticado != null
                && autenticado.getPerfil() == PerfilEnum.ADMIN;
        boolean ehProprioUsuario = autenticado != null
                && request.id().equals(autenticado.getId());

        if (!ehAdmin && !ehProprioUsuario) {
            throw new AuthorizationException("Voce so' pode alterar a propria senha");
        }
        usuarioUseCase.alterarSenha(
                new AlterarSenhaCommand(request.id(), request.oldPassword(), request.newPassword()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar usuario")
    public ResponseEntity<Void> inativar(@PathVariable Long id,
                                         @AuthenticationPrincipal(expression = USUARIO_AUTENTICADO) Usuario autenticado) {
        if (autenticado != null && id.equals(autenticado.getId())) {
            throw new RegraDeNegocioException("Voce nao pode inativar a si mesmo");
        }
        usuarioUseCase.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivate")
    @Operation(summary = "Reativar usuario")
    public ResponseEntity<UserResponse> reativar(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(usuarioUseCase.reativar(id)));
    }
}
