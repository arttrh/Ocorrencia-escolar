package br.com.project_sena.application.core.domain.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import br.com.project_sena.application.core.domain.enums.OcorrenciaEnum;
import br.com.project_sena.application.core.domain.exception.OcorrenciaCanceladaException;
import br.com.project_sena.application.core.domain.exception.RegraDeNegocioException;
import br.com.project_sena.application.core.domain.exception.TransicaoStatusInvalidaException;

/**
 * Ocorrencia disciplinar registrada para um aluno de uma turma.
 *
 * <p>Concentra as regras da ocorrencia: obrigatoriedade dos relacionamentos, limite da
 * descricao, data de registro nao futura, exclusao logica e a maquina de estados do
 * atendimento. Nenhum use case pode pular essas regras porque nao existe setter publico.</p>
 */
public class Ocorrencia {

    public static final int TAMANHO_MAXIMO_DESCRICAO = 500;

    /** Transicoes permitidas. Status ausente do mapa (ou com destino vazio) e' terminal. */
    private static final Map<OcorrenciaEnum, Set<OcorrenciaEnum>> TRANSICOES = Map.of(
            OcorrenciaEnum.AGUARDANDO, Set.of(
                    OcorrenciaEnum.ATENDENDO, OcorrenciaEnum.ATIVA, OcorrenciaEnum.FECHADA),
            OcorrenciaEnum.ATIVA, Set.of(
                    OcorrenciaEnum.ATENDENDO, OcorrenciaEnum.RESOLVIDA,
                    OcorrenciaEnum.NAO_RESOLVIDA, OcorrenciaEnum.FECHADA),
            OcorrenciaEnum.ATENDENDO, Set.of(
                    OcorrenciaEnum.ATIVA, OcorrenciaEnum.RESOLVIDA,
                    OcorrenciaEnum.NAO_RESOLVIDA, OcorrenciaEnum.FECHADA),
            OcorrenciaEnum.RESOLVIDA, Set.of(),
            OcorrenciaEnum.NAO_RESOLVIDA, Set.of(),
            OcorrenciaEnum.FECHADA, Set.of());

    private Long id;
    private Turma turma;
    private Aluno student;
    private CategoriaOcorrencia category;
    private TipoOcorrencia type;
    private LocalDateTime registerDate;
    private String description;
    private OcorrenciaEnum status;
    private LocalDateTime updateDate;
    private boolean deleted;
    private LocalDateTime createdAt;

    public Ocorrencia(Long id,
                      Turma turma,
                      Aluno student,
                      CategoriaOcorrencia category,
                      TipoOcorrencia type,
                      LocalDateTime registerDate,
                      String description,
                      OcorrenciaEnum status,
                      LocalDateTime updateDate,
                      boolean deleted,
                      LocalDateTime createdAt) {
        this.id = id;
        this.turma = Objects.requireNonNull(turma, "Turma da ocorrencia e' obrigatoria");
        this.student = Objects.requireNonNull(student, "Aluno da ocorrencia e' obrigatorio");
        this.category = Objects.requireNonNull(category, "Categoria da ocorrencia e' obrigatoria");
        this.type = Objects.requireNonNull(type, "Tipo da ocorrencia e' obrigatorio");
        this.registerDate = exigirDataValida(registerDate);
        this.description = exigirDescricaoValida(description);
        this.status = status == null ? OcorrenciaEnum.AGUARDANDO : status;
        this.updateDate = updateDate;
        this.deleted = deleted;
        this.createdAt = createdAt;
    }

    /**
     * Cria uma ocorrencia nova: nasce {@link OcorrenciaEnum#AGUARDANDO} e sem data de
     * registro obriga a usar o instante atual.
     */
    public static Ocorrencia nova(Turma turma,
                                  Aluno student,
                                  CategoriaOcorrencia category,
                                  TipoOcorrencia type,
                                  LocalDateTime registerDate,
                                  String description) {
        return new Ocorrencia(
                null,
                turma,
                student,
                category,
                type,
                registerDate == null ? LocalDateTime.now() : registerDate,
                description,
                OcorrenciaEnum.AGUARDANDO,
                null,
                false,
                null);
    }

    public Long getId() {
        return id;
    }

    public Turma getTurma() {
        return turma;
    }

    public Aluno getStudent() {
        return student;
    }

    public CategoriaOcorrencia getCategory() {
        return category;
    }

    public TipoOcorrencia getType() {
        return type;
    }

    public LocalDateTime getRegisterDate() {
        return registerDate;
    }

    public String getDescription() {
        return description;
    }

    public OcorrenciaEnum getStatus() {
        return status;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /** Atualizacao parcial: argumentos nulos/em branco preservam o valor atual. */
    public void atualizarDados(Turma turma,
                               Aluno student,
                               CategoriaOcorrencia category,
                               TipoOcorrencia type,
                               LocalDateTime registerDate,
                               String description) {
        garantirNaoCancelada();
        if (turma != null) {
            this.turma = turma;
        }
        if (student != null) {
            this.student = student;
        }
        if (category != null) {
            this.category = category;
        }
        if (type != null) {
            this.type = type;
        }
        if (registerDate != null) {
            this.registerDate = exigirDataValida(registerDate);
        }
        if (description != null && !description.isBlank()) {
            this.description = exigirDescricaoValida(description);
        }
        this.updateDate = LocalDateTime.now();
    }

    /**
     * Aplica a maquina de estados do atendimento.
     *
     * @throws TransicaoStatusInvalidaException se o destino nao for alcancavel a partir
     *                                          do status atual
     */
    public void alterarStatus(OcorrenciaEnum novoStatus, LocalDateTime quando) {
        garantirNaoCancelada();
        Objects.requireNonNull(novoStatus, "Novo status e' obrigatorio");

        if (novoStatus == this.status) {
            throw new TransicaoStatusInvalidaException(
                    "Ocorrencia ja esta com o status " + this.status.getDescricao());
        }
        if (!TRANSICOES.getOrDefault(this.status, Set.of()).contains(novoStatus)) {
            throw new TransicaoStatusInvalidaException(
                    "Nao e' possivel mudar de " + this.status.getDescricao()
                            + " para " + novoStatus.getDescricao());
        }
        this.status = novoStatus;
        this.updateDate = quando == null ? LocalDateTime.now() : quando;
    }

    /** Exclusao logica: a ocorrencia some das listagens mas o historico e' preservado. */
    public void cancelar() {
        garantirNaoCancelada();
        this.deleted = true;
        this.updateDate = LocalDateTime.now();
    }

    public void garantirNaoCancelada() {
        if (deleted) {
            throw new OcorrenciaCanceladaException("Ocorrencia cancelada nao pode ser alterada");
        }
    }

    private static LocalDateTime exigirDataValida(LocalDateTime registerDate) {
        if (registerDate == null) {
            throw new RegraDeNegocioException("Data de registro da ocorrencia e' obrigatoria");
        }
        if (registerDate.isAfter(LocalDateTime.now().plusMinutes(5))) {
            throw new RegraDeNegocioException("Data de registro nao pode estar no futuro");
        }
        return registerDate;
    }

    private static String exigirDescricaoValida(String description) {
        if (description == null || description.isBlank()) {
            throw new RegraDeNegocioException("Descricao da ocorrencia e' obrigatoria");
        }
        String texto = description.trim();
        if (texto.length() > TAMANHO_MAXIMO_DESCRICAO) {
            throw new RegraDeNegocioException(
                    "Descricao da ocorrencia deve ter no maximo "
                            + TAMANHO_MAXIMO_DESCRICAO + " caracteres");
        }
        return texto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ocorrencia outra)) {
            return false;
        }
        return id != null && Objects.equals(id, outra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
