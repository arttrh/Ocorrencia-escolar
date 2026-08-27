package br.com.project_sena.application.core.domain.model;

import java.time.LocalDate;
import java.util.Objects;

import br.com.project_sena.application.core.domain.enums.SemestreEnum;
import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import br.com.project_sena.application.core.domain.enums.TurmaTurnoEnum;
import br.com.project_sena.application.core.domain.exception.RegraDeNegocioException;
import br.com.project_sena.application.core.domain.exception.TurmaCanceladaException;
import br.com.project_sena.application.core.domain.exception.TurmaCheiaException;

public class Turma {

    /** Capacidade maxima de alunos por turma. */
    public static final int CAPACIDADE_MAXIMA = 36;

    private static final int ANO_MINIMO = 2000;

    private Long id;
    private String name;
    private TurmaTurnoEnum shift;
    private Integer year;
    private SemestreEnum semester;
    private TurmaEnum status;

    public Turma(Long id, String name, TurmaTurnoEnum shift, Integer year, SemestreEnum semester, TurmaEnum status) {
        this.id = id;
        this.name = exigirTexto(name, "Nome da turma e' obrigatorio");
        this.shift = Objects.requireNonNull(shift, "Turno da turma e' obrigatorio");
        this.year = exigirAnoValido(year);
        this.semester = Objects.requireNonNull(semester, "Semestre da turma e' obrigatorio");
        this.status = status == null ? TurmaEnum.ATIVA : status;
    }

    public static Turma nova(String name, TurmaTurnoEnum shift, Integer year, SemestreEnum semester) {
        return new Turma(null, name, shift, year, semester, TurmaEnum.ATIVA);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TurmaTurnoEnum getShift() {
        return shift;
    }

    public Integer getYear() {
        return year;
    }

    public SemestreEnum getSemester() {
        return semester;
    }

    public TurmaEnum getStatus() {
        return status;
    }

    public boolean isAtiva() {
        return status.isAtiva();
    }

    public boolean isCancelada() {
        return !isAtiva();
    }

    public void atualizarDados(String name, TurmaTurnoEnum shift, Integer year, SemestreEnum semester) {
        if (name != null && !name.isBlank()) {
            this.name = name.trim();
        }
        if (shift != null) {
            this.shift = shift;
        }
        if (year != null) {
            this.year = exigirAnoValido(year);
        }
        if (semester != null) {
            this.semester = semester;
        }
    }

    /**
     * Garante que a turma pode receber mais um aluno.
     *
     * @param alunosMatriculados quantidade atual de vinculos, consultada pelo use case
     */
    public void garantirVagaDisponivel(long alunosMatriculados) {
        if (isCancelada()) {
            throw new TurmaCanceladaException("Turma cancelada nao pode receber alunos");
        }
        if (alunosMatriculados >= CAPACIDADE_MAXIMA) {
            throw new TurmaCheiaException(
                    "Turma ja atingiu a capacidade maxima de " + CAPACIDADE_MAXIMA + " alunos");
        }
    }

    public void cancelar() {
        this.status = TurmaEnum.CANCELADA;
    }

    public void reativar() {
        if (isAtiva()) {
            throw new RegraDeNegocioException("Turma ja esta ativa");
        }
        this.status = TurmaEnum.ATIVA;
    }

    private static Integer exigirAnoValido(Integer year) {
        if (year == null) {
            throw new RegraDeNegocioException("Ano da turma e' obrigatorio");
        }
        int anoLimite = LocalDate.now().getYear() + 1;
        if (year < ANO_MINIMO || year > anoLimite) {
            throw new RegraDeNegocioException(
                    "Ano da turma deve estar entre " + ANO_MINIMO + " e " + anoLimite);
        }
        return year;
    }

    private static String exigirTexto(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new RegraDeNegocioException(mensagem);
        }
        return valor.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Turma outra)) {
            return false;
        }
        return id != null && Objects.equals(id, outra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
