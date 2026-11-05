package br.com.project_sena.adapter.in.controller;
import br.com.project_sena.adapter.in.controller.request.OccurrenceRequestDTO;
import br.com.project_sena.application.core.domain.enums.OcorrenciaEnum;
import br.com.project_sena.application.core.domain.model.*;
import br.com.project_sena.application.core.usecase.OcorrenciaService;
import br.com.project_sena.application.port.out.CategoriaOcorrenciaRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.*;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/ocorrencias")
public class OcorrenciaController {
    private final OcorrenciaService service;
    private final CategoriaOcorrenciaRepository categorias;
    public OcorrenciaController(OcorrenciaService service, CategoriaOcorrenciaRepository categorias) {
        this.service = service;
        this.categorias = categorias;
    }
    @GetMapping("/resumo")
    public Map<OcorrenciaEnum, Long> resumo() {
        return service.resumo();
    }
    @GetMapping
    public Page<Detalhes> listar(@RequestParam(required = false) OcorrenciaEnum status,
        @PageableDefault(sort = "id") Pageable pagina) {
        return service.listar(status, pagina).map(Detalhes::new);
    }
    @GetMapping("/{id}")
    public Detalhes buscar(@PathVariable Long id) {
        return new Detalhes(service.buscar(id));
    }
    @PostMapping
    public ResponseEntity<Detalhes> cadastrar(@RequestBody @Valid OccurrenceRequestDTO dto) {
        Ocorrencia saved = salvar(null, dto);
        return ResponseEntity.created(java.net.URI.create("/ocorrencias/" + saved.getId()))
            .body(new Detalhes(saved));
    }
    @PutMapping("/{id}")
    public Detalhes atualizar(@PathVariable Long id, @RequestBody @Valid OccurrenceRequestDTO dto) {
        return new Detalhes(salvar(id, dto));
    }
    private Ocorrencia salvar(Long id, OccurrenceRequestDTO dto) {
        return service.salvar(id, dto.studentId(), dto.classId(), dto.categoryId(), dto.occurenceId(),
            dto.date(), dto.time(), dto.descriptionOccurrence());
    }
    @PatchMapping("/{id}/status")
    public Detalhes status(@PathVariable Long id, @RequestBody @Valid Status dto) {
        return new Detalhes(service.alterarStatus(id, dto.status()));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        service.alterarStatus(id, OcorrenciaEnum.CANCELADAS);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/categorias")
    public List<CategoriaOcorrencia> categorias() {
        return categorias.listar();
    }
    @GetMapping("/categorias/{id}/tipos")
    public List<TipoOcorrencia> tipos(@PathVariable Long id) {
        return categorias.tipos(id);
    }
    @GetMapping("/status")
    public OcorrenciaEnum[] status() {
        return OcorrenciaEnum.values();
    }
    public record Status(@NotNull OcorrenciaEnum status) {}
    public record Detalhes(Long id, Long studentId, Long classId, Long categoryId, Long occurenceId,
        String student, String schoolClass, String category, String type, LocalDate date, LocalTime time,
        String descriptionOccurrence, OcorrenciaEnum status, LocalDateTime createdAt) {
        public Detalhes(Ocorrencia o) {
            this(o.getId(), o.getStudent().getId(), o.getTurma().getId(), o.getCategory().getId(),
                o.getOccurrenceType().getId(), o.getStudent().getName(), o.getTurma().getClassName(),
                o.getCategory().getNameCategory(), o.getOccurrenceType().getNameOccurrence(),
                o.getDataOcorrencia(), o.getTime(), o.getDescricaoDaOcorrencia(), o.getOcorrenciaEnum(),
                o.getCreatedAt());
        }
    }
}
