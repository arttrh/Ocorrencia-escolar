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

import br.com.project_sena.adapter.in.web.dto.request.IncidentRegisterRequest;
import br.com.project_sena.adapter.in.web.dto.request.IncidentStatusRequest;
import br.com.project_sena.adapter.in.web.dto.request.IncidentUpdateRequest;
import br.com.project_sena.adapter.in.web.dto.response.EnumResponse;
import br.com.project_sena.adapter.in.web.dto.response.IncidentResponse;
import br.com.project_sena.adapter.in.web.dto.response.IncidentSummaryResponse;
import br.com.project_sena.adapter.in.web.dto.response.PageResponse;
import br.com.project_sena.adapter.in.web.mapper.OcorrenciaWebMapper;
import br.com.project_sena.adapter.in.web.mapper.PaginacaoWeb;
import br.com.project_sena.application.core.domain.enums.OcorrenciaEnum;
import br.com.project_sena.application.core.domain.exception.RegraDeNegocioException;
import br.com.project_sena.application.core.domain.model.CategoriaOcorrencia;
import br.com.project_sena.application.core.domain.model.Ocorrencia;
import br.com.project_sena.application.core.domain.model.TipoOcorrencia;
import br.com.project_sena.application.port.in.OcorrenciaUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Ocorrencias disciplinares.
 *
 * <p>Recurso que nao existia no back-end: o front ja chamava {@code /incidents} em todas
 * as suas telas de ocorrencia e recebia 404 em todas elas.</p>
 */
@RestController
@RequestMapping("/incidents")
@Tag(name = "Ocorrencias", description = "Registro e acompanhamento de ocorrencias")
public class OcorrenciaController {

    private final OcorrenciaUseCase ocorrenciaUseCase;
    private final OcorrenciaWebMapper mapper;

    public OcorrenciaController(OcorrenciaUseCase ocorrenciaUseCase, OcorrenciaWebMapper mapper) {
        this.ocorrenciaUseCase = ocorrenciaUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    @Operation(summary = "Registrar ocorrencia")
    public ResponseEntity<IncidentResponse> cadastrar(@RequestBody @Valid IncidentRegisterRequest request,
                                                      UriComponentsBuilder uriBuilder) {
        Ocorrencia ocorrencia = ocorrenciaUseCase.cadastrar(mapper.toCommand(request));
        URI uri = uriBuilder.path("/incidents/{id}").buildAndExpand(ocorrencia.getId()).toUri();
        return ResponseEntity.created(uri).body(mapper.toResponse(ocorrencia));
    }

    @GetMapping
    @Operation(summary = "Listar ocorrencias", description = "Exclui as canceladas")
    public ResponseEntity<PageResponse<IncidentResponse>> listar(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction) {
        return ResponseEntity.ok(PageResponse.de(
                ocorrenciaUseCase.listar(PaginacaoWeb.de(page, size, sort, direction)),
                mapper::toResponse));
    }

    /**
     * Situacoes possiveis, com rotulo legivel.
     *
     * <p>Formato {@code {name, description}} porque e' o que a tela de mudanca de status
     * consome ({@code option.value = item.name}, texto {@code item.description}).</p>
     */
    @GetMapping("/status")
    @Operation(summary = "Situacoes de ocorrencia")
    public ResponseEntity<List<EnumResponse>> status() {
        return ResponseEntity.ok(Arrays.stream(OcorrenciaEnum.values())
                .map(status -> new EnumResponse(status.name(), status.getDescricao()))
                .toList());
    }

    /**
     * Listagem por situacao. Aceita tanto o slug publico ({@code waiting}) quanto o nome
     * da constante ({@code AGUARDANDO}).
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Listar ocorrencias por situacao")
    public ResponseEntity<PageResponse<IncidentResponse>> listarPorStatus(
            @PathVariable String status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction) {
        OcorrenciaEnum situacao = OcorrenciaEnum.porSlugOuNome(status);
        if (situacao == null) {
            throw new RegraDeNegocioException("Situacao de ocorrencia desconhecida: " + status);
        }
        return ResponseEntity.ok(PageResponse.de(
                ocorrenciaUseCase.listarPorStatus(situacao, PaginacaoWeb.de(page, size, sort, direction)),
                mapper::toResponse));
    }

    @GetMapping("/summary")
    @Operation(summary = "Resumo para o dashboard")
    public ResponseEntity<IncidentSummaryResponse> resumo() {
        return ResponseEntity.ok(mapper.toResponse(ocorrenciaUseCase.resumo()));
    }

    @GetMapping("/categories")
    @Operation(summary = "Categorias de ocorrencia")
    public ResponseEntity<List<String>> categorias() {
        return ResponseEntity.ok(ocorrenciaUseCase.listarCategorias().stream()
                .map(CategoriaOcorrencia::getName)
                .toList());
    }

    @GetMapping("/types/{category}")
    @Operation(summary = "Tipos de uma categoria")
    public ResponseEntity<List<String>> tipos(@PathVariable String category) {
        return ResponseEntity.ok(ocorrenciaUseCase.listarTiposPorCategoria(category).stream()
                .map(TipoOcorrencia::getName)
                .toList());
    }

    @GetMapping("/students/{studentId}/history")
    @Operation(summary = "Historico de ocorrencias do aluno")
    public ResponseEntity<List<IncidentResponse>> historico(@PathVariable Long studentId) {
        return ResponseEntity.ok(ocorrenciaUseCase.historicoDoAluno(studentId).stream()
                .map(mapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhar ocorrencia")
    public ResponseEntity<IncidentResponse> detalhar(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toResponse(ocorrenciaUseCase.buscar(id)));
    }

    @PutMapping
    @Operation(summary = "Atualizar ocorrencia")
    public ResponseEntity<IncidentResponse> atualizar(@RequestBody @Valid IncidentUpdateRequest request) {
        return ResponseEntity.ok(
                mapper.toResponse(ocorrenciaUseCase.atualizar(mapper.toCommand(request))));
    }

    @PatchMapping("/status")
    @Operation(summary = "Mudar a situacao da ocorrencia")
    public ResponseEntity<IncidentResponse> alterarStatus(
            @RequestBody @Valid IncidentStatusRequest request) {
        return ResponseEntity.ok(
                mapper.toResponse(ocorrenciaUseCase.alterarStatus(mapper.toCommand(request))));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar ocorrencia", description = "Exclusao logica")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        ocorrenciaUseCase.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
