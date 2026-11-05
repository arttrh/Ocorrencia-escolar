package br.com.project_sena.application.core.usecase;
import br.com.project_sena.application.core.domain.enums.OcorrenciaEnum;
import br.com.project_sena.application.core.domain.model.*;
import br.com.project_sena.application.core.usecase.validacoes.ValidarOcorrencia;
import br.com.project_sena.application.port.out.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.*;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
@Service
public class OcorrenciaService {
    private final OcorrenciaRepository repository;
    private final AlunoService alunos;
    private final TurmaService turmas;
    private final CategoriaOcorrenciaRepository categorias;
    private final ValidarOcorrencia validar;
    public OcorrenciaService(OcorrenciaRepository repository, AlunoService alunos, TurmaService turmas,
        CategoriaOcorrenciaRepository categorias, ValidarOcorrencia validar) {
        this.repository = repository;
        this.alunos = alunos;
        this.turmas = turmas;
        this.categorias = categorias;
        this.validar = validar;
    }
    public Map<OcorrenciaEnum, Long> resumo() {
        Map<OcorrenciaEnum, Long> resumo = new EnumMap<>(OcorrenciaEnum.class);
        for (OcorrenciaEnum status : OcorrenciaEnum.values()) resumo.put(status, repository.contar(status));
        return resumo;
    }
    public Ocorrencia buscar(Long id) {
        return repository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("Ocorrência não encontrada"));
    }
    public Page<Ocorrencia> listar(OcorrenciaEnum status, Pageable pagina) {
        return status == null ? repository.listar(pagina) : repository.listar(status, pagina);
    }
    @Transactional
    public Ocorrencia salvar(Long id, Long alunoId, Long turmaId, Long categoriaId, Long tipoId,
        LocalDate data, LocalTime hora, String descricao) {
        Ocorrencia anterior = id == null ? null : buscar(id);
        if (anterior != null && anterior.getOcorrenciaEnum() == OcorrenciaEnum.CANCELADAS)
            throw new IllegalArgumentException("Ocorrência cancelada não pode ser editada");
        Aluno aluno = alunos.buscar(alunoId);
        Turma turma = turmas.buscar(turmaId);
        validar.validar(aluno, turma);
        CategoriaOcorrencia categoria =
            categorias.buscar(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria inválida"));
        TipoOcorrencia tipo =
            categorias.buscarTipo(tipoId, categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Tipo não pertence à categoria"));
        return repository.save(new Ocorrencia(id, turma, aluno, categoria, tipo, data, hora, descricao,
            anterior == null ? OcorrenciaEnum.AGUARDANDO : anterior.getOcorrenciaEnum(),
            anterior == null ? LocalDateTime.now() : anterior.getCreatedAt()));
    }
    @Transactional
    public Ocorrencia alterarStatus(Long id, OcorrenciaEnum status) {
        Ocorrencia anterior = buscar(id);
        if (anterior.getOcorrenciaEnum() == OcorrenciaEnum.CANCELADAS)
            throw new IllegalArgumentException("Ocorrência já cancelada");
        return repository.save(new Ocorrencia(id, anterior.getTurma(), anterior.getStudent(),
            anterior.getCategory(), anterior.getOccurrenceType(), anterior.getDataOcorrencia(),
            anterior.getTime(), anterior.getDescricaoDaOcorrencia(), status, anterior.getCreatedAt()));
    }
}
