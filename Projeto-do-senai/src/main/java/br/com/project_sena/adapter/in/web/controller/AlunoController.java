package br.com.project_sena.adapter.in.web.controller;

import java.net.URI;
import java.util.Base64;
import java.util.List;
import java.util.Set;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.project_sena.adapter.in.web.dto.request.StudentRegisterRequest;
import br.com.project_sena.adapter.in.web.dto.request.StudentUpdateRequest;
import br.com.project_sena.adapter.in.web.dto.response.PageResponse;
import br.com.project_sena.adapter.in.web.dto.response.StudentResponse;
import br.com.project_sena.adapter.in.web.mapper.AlunoWebMapper;
import br.com.project_sena.adapter.in.web.mapper.PaginacaoWeb;
import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.exception.RegraDeNegocioException;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.port.in.AlunoUseCase;
import br.com.project_sena.application.port.in.command.AlterarFotoAlunoCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/students")
@Tag(name = "Alunos", description = "Gerenciamento de alunos")
public class AlunoController {

    /** Tipos aceitos no upload da foto. */
    private static final Set<String> TIPOS_DE_IMAGEM =
            Set.of("image/png", "image/jpeg", "image/webp");

    /** 2 MB: a foto e' guardada como data URI na propria linha do aluno. */
    private static final long TAMANHO_MAXIMO_IMAGEM = 2L * 1024 * 1024;

    private final AlunoUseCase alunoUseCase;
    private final AlunoWebMapper mapper;

    public AlunoController(AlunoUseCase alunoUseCase, AlunoWebMapper mapper) {
        this.alunoUseCase = alunoUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Cadastrar aluno")
    public ResponseEntity<StudentResponse> cadastrar(@RequestBody @Valid StudentRegisterRequest request,
                                                     UriComponentsBuilder uriBuilder) {
        Aluno aluno = alunoUseCase.cadastrar(mapper.toCommand(request));
        URI uri = uriBuilder.path("/students/{id}").buildAndExpand(aluno.getId()).toUri();
        return ResponseEntity.created(uri).body(mapper.toResponse(aluno));
    }

    @GetMapping
    @Operation(summary = "Listar alunos ativos")
    public ResponseEntity<PageResponse<StudentResponse>> listarAtivos(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction) {
        return ResponseEntity.ok(PageResponse.de(
                alunoUseCase.listar(AlunoEnum.ATIVO, PaginacaoWeb.de(page, size, sort, direction)),
                mapper::toResponse));
    }

    @GetMapping("/inactive")
    @Operation(summary = "Listar alunos inativos")
    public ResponseEntity<PageResponse<StudentResponse>> listarInativos(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction) {
        return ResponseEntity.ok(PageResponse.de(
                alunoUseCase.listar(AlunoEnum.INATIVO, PaginacaoWeb.de(page, size, sort, direction)),
                mapper::toResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhar aluno")
    public ResponseEntity<StudentResponse> detalhar(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(alunoUseCase.buscar(id)));
    }

    @PutMapping
    @Operation(summary = "Atualizar aluno")
    public ResponseEntity<StudentResponse> atualizar(@RequestBody @Valid StudentUpdateRequest request) {
        return ResponseEntity.ok(mapper.toResponse(alunoUseCase.atualizar(mapper.toCommand(request))));
    }

    /**
     * Envio da foto do aluno.
     *
     * <p>A imagem e' convertida em data URI e guardada junto do aluno — solucao adequada
     * ao volume deste sistema e que evita depender de disco ou de um bucket externo. O
     * tipo e o tamanho sao conferidos aqui: aceitar qualquer arquivo permitiria injetar
     * SVG com script, que o navegador executaria ao renderizar a foto.</p>
     */
    @PatchMapping("/{id}/image")
    @Operation(summary = "Enviar foto do aluno")
    public ResponseEntity<StudentResponse> enviarFoto(@PathVariable Long id,
                                                      @RequestParam("image") MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new RegraDeNegocioException("Nenhuma imagem enviada");
        }
        if (image.getSize() > TAMANHO_MAXIMO_IMAGEM) {
            throw new RegraDeNegocioException("A imagem deve ter no maximo 2 MB");
        }
        String contentType = image.getContentType();
        if (contentType == null || !TIPOS_DE_IMAGEM.contains(contentType.toLowerCase())) {
            throw new RegraDeNegocioException(
                    "Formato de imagem nao suportado. Use: " + String.join(", ", TIPOS_DE_IMAGEM));
        }
        try {
            String dataUri = "data:" + contentType + ";base64,"
                    + Base64.getEncoder().encodeToString(image.getBytes());
            return ResponseEntity.ok(mapper.toResponse(
                    alunoUseCase.alterarFoto(new AlterarFotoAlunoCommand(id, dataUri))));
        } catch (java.io.IOException e) {
            throw new RegraDeNegocioException("Nao foi possivel ler a imagem enviada");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar aluno")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        alunoUseCase.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivate")
    @Operation(summary = "Reativar aluno")
    public ResponseEntity<StudentResponse> reativar(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(alunoUseCase.reativar(id)));
    }

    @GetMapping("/formats")
    @Operation(summary = "Formatos de imagem aceitos")
    public ResponseEntity<List<String>> formatosDeImagem() {
        return ResponseEntity.ok(TIPOS_DE_IMAGEM.stream().sorted().toList());
    }
}
