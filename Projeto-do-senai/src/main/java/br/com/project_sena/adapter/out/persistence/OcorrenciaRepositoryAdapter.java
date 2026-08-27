package br.com.project_sena.adapter.out.persistence;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.project_sena.adapter.out.persistence.entity.AlunoEntity;
import br.com.project_sena.adapter.out.persistence.entity.CategoriaOcorrenciaEntity;
import br.com.project_sena.adapter.out.persistence.entity.OcorrenciaEntity;
import br.com.project_sena.adapter.out.persistence.entity.TipoOcorrenciaEntity;
import br.com.project_sena.adapter.out.persistence.entity.TurmaEntity;
import br.com.project_sena.adapter.out.persistence.jpa.AlunoJpaRepository;
import br.com.project_sena.adapter.out.persistence.jpa.CategoriaOcorrenciaJpaRepository;
import br.com.project_sena.adapter.out.persistence.jpa.OcorrenciaJpaRepository;
import br.com.project_sena.adapter.out.persistence.jpa.TipoOcorrenciaJpaRepository;
import br.com.project_sena.adapter.out.persistence.jpa.TurmaJpaRepository;
import br.com.project_sena.adapter.out.persistence.mapper.OcorrenciaMapperEntity;
import br.com.project_sena.application.core.domain.enums.OcorrenciaEnum;
import br.com.project_sena.application.core.domain.exception.AlunoNotFoundException;
import br.com.project_sena.application.core.domain.exception.CategoriaNotFoundException;
import br.com.project_sena.application.core.domain.exception.TipoOcorrenciaNotFoundException;
import br.com.project_sena.application.core.domain.exception.TurmaNotFoundException;
import br.com.project_sena.application.core.domain.model.Ocorrencia;
import br.com.project_sena.application.core.domain.vo.ContagemPorChave;
import br.com.project_sena.application.core.domain.vo.Pagina;
import br.com.project_sena.application.core.domain.vo.PaginaRequest;
import br.com.project_sena.application.port.out.OcorrenciaRepository;

@Component
public class OcorrenciaRepositoryAdapter implements OcorrenciaRepository {

    /**
     * Campos de ordenacao aceitos pela API mapeados para o caminho da entidade. Os nomes
     * da esquerda sao exatamente os que o {@code <select id="select-ordenacao">} do front
     * envia na query string.
     */
    private static final Map<String, String> ORDENACAO = Map.of(
            "id", "id",
            "registerDate", "registerDate",
            "updateDate", "updateDate",
            "status", "status",
            "category", "category.name",
            "type", "type.name",
            "schoolClass.name", "turma.name",
            "student.name", "student.name");

    private final OcorrenciaJpaRepository jpaRepository;
    private final TurmaJpaRepository turmaJpaRepository;
    private final AlunoJpaRepository alunoJpaRepository;
    private final CategoriaOcorrenciaJpaRepository categoriaJpaRepository;
    private final TipoOcorrenciaJpaRepository tipoJpaRepository;
    private final OcorrenciaMapperEntity mapper;

    public OcorrenciaRepositoryAdapter(OcorrenciaJpaRepository jpaRepository,
                                       TurmaJpaRepository turmaJpaRepository,
                                       AlunoJpaRepository alunoJpaRepository,
                                       CategoriaOcorrenciaJpaRepository categoriaJpaRepository,
                                       TipoOcorrenciaJpaRepository tipoJpaRepository,
                                       OcorrenciaMapperEntity mapper) {
        this.jpaRepository = jpaRepository;
        this.turmaJpaRepository = turmaJpaRepository;
        this.alunoJpaRepository = alunoJpaRepository;
        this.categoriaJpaRepository = categoriaJpaRepository;
        this.tipoJpaRepository = tipoJpaRepository;
        this.mapper = mapper;
    }

    /**
     * Grava o agregado.
     *
     * <p>Numa atualizacao a entidade existente e' carregada e mutada, em vez de montar uma
     * nova a partir do dominio: e' o que preserva {@code created_at} e evita que o
     * Hibernate trate o registro como novo.</p>
     */
    @Override
    public Ocorrencia save(Ocorrencia ocorrencia) {
        OcorrenciaEntity entity = ocorrencia.getId() == null
                ? new OcorrenciaEntity()
                : jpaRepository.findById(ocorrencia.getId()).orElseGet(OcorrenciaEntity::new);

        entity.setTurma(turmaGerenciada(ocorrencia));
        entity.setStudent(alunoGerenciado(ocorrencia));
        entity.setCategory(categoriaGerenciada(ocorrencia));
        entity.setType(tipoGerenciado(ocorrencia));
        entity.setRegisterDate(ocorrencia.getRegisterDate());
        entity.setDescription(ocorrencia.getDescription());
        entity.setStatus(ocorrencia.getStatus());
        entity.setUpdateDate(ocorrencia.getUpdateDate());
        entity.setDeleted(ocorrencia.isDeleted());

        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Ocorrencia> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Pagina<Ocorrencia> findAtivas(PaginaRequest paginaRequest) {
        return PaginacaoSpring.paraPagina(
                jpaRepository.findAllByDeletedFalse(
                        PaginacaoSpring.paraPageable(paginaRequest, ORDENACAO)),
                mapper::toDomain);
    }

    @Override
    public Pagina<Ocorrencia> findByStatus(OcorrenciaEnum status, PaginaRequest paginaRequest) {
        return PaginacaoSpring.paraPagina(
                jpaRepository.findAllByStatusAndDeletedFalse(
                        status, PaginacaoSpring.paraPageable(paginaRequest, ORDENACAO)),
                mapper::toDomain);
    }

    @Override
    public List<Ocorrencia> findByAlunoId(Long alunoId) {
        return jpaRepository.findAllByStudentIdAndDeletedFalse(alunoId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public long contarPorStatus(OcorrenciaEnum status) {
        return jpaRepository.countByStatusAndDeletedFalse(status);
    }

    @Override
    public List<ContagemPorChave> contarPorCategoria() {
        return jpaRepository.contarPorCategoria();
    }

    @Override
    public List<ContagemPorChave> contarPorTipo() {
        return jpaRepository.contarPorTipo();
    }

    @Override
    public List<ContagemPorChave> contarPorTurma() {
        return jpaRepository.contarPorTurma();
    }

    @Override
    public List<ContagemPorChave> contarPorAluno() {
        return jpaRepository.contarPorAluno();
    }

    private TurmaEntity turmaGerenciada(Ocorrencia ocorrencia) {
        Long id = ocorrencia.getTurma().getId();
        return turmaJpaRepository.findById(id)
                .orElseThrow(() -> new TurmaNotFoundException("Turma nao encontrada: " + id));
    }

    private AlunoEntity alunoGerenciado(Ocorrencia ocorrencia) {
        Long id = ocorrencia.getStudent().getId();
        return alunoJpaRepository.findById(id)
                .orElseThrow(() -> new AlunoNotFoundException("Aluno nao encontrado: " + id));
    }

    private CategoriaOcorrenciaEntity categoriaGerenciada(Ocorrencia ocorrencia) {
        Long id = ocorrencia.getCategory().getId();
        return categoriaJpaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException("Categoria nao encontrada: " + id));
    }

    private TipoOcorrenciaEntity tipoGerenciado(Ocorrencia ocorrencia) {
        Long id = ocorrencia.getType().getId();
        return tipoJpaRepository.findById(id)
                .orElseThrow(() -> new TipoOcorrenciaNotFoundException(
                        "Tipo de ocorrencia nao encontrado: " + id));
    }
}
