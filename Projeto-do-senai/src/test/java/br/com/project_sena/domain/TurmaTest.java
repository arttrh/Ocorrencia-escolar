package br.com.project_sena.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.project_sena.application.core.domain.enums.SemestreEnum;
import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import br.com.project_sena.application.core.domain.enums.TurmaTurnoEnum;
import br.com.project_sena.application.core.domain.exception.RegraDeNegocioException;
import br.com.project_sena.application.core.domain.exception.TurmaCanceladaException;
import br.com.project_sena.application.core.domain.exception.TurmaCheiaException;
import br.com.project_sena.application.core.domain.model.Turma;

@DisplayName("Turma (dominio)")
class TurmaTest {

    private static Turma ativa() {
        return new Turma(1L, "DS-01", TurmaTurnoEnum.MANHA, LocalDate.now().getYear(),
                SemestreEnum.PRIMEIRO, TurmaEnum.ATIVA);
    }

    @Test
    @DisplayName("aceita aluno enquanto ha vaga")
    void aceitaEnquantoHaVaga() {
        assertDoesNotThrow(() -> ativa().garantirVagaDisponivel(Turma.CAPACIDADE_MAXIMA - 1));
    }

    @Test
    @DisplayName("recusa aluno quando atinge a capacidade maxima")
    void recusaQuandoCheia() {
        TurmaCheiaException erro = assertThrows(TurmaCheiaException.class,
                () -> ativa().garantirVagaDisponivel(Turma.CAPACIDADE_MAXIMA));

        assertTrue(erro.getMessage().contains(String.valueOf(Turma.CAPACIDADE_MAXIMA)));
    }

    @Test
    @DisplayName("turma cancelada nao recebe alunos, mesmo com vaga")
    void canceladaNaoRecebeAlunos() {
        Turma turma = ativa();
        turma.cancelar();

        assertThrows(TurmaCanceladaException.class, () -> turma.garantirVagaDisponivel(0));
    }

    @Test
    @DisplayName("reativar uma turma ja ativa e' erro de negocio")
    void naoReativaTurmaAtiva() {
        assertThrows(RegraDeNegocioException.class, () -> ativa().reativar());
    }

    @Test
    @DisplayName("cancelar e reativar volta ao estado ativo")
    void cancelaEReativa() {
        Turma turma = ativa();

        turma.cancelar();
        assertTrue(turma.isCancelada());

        turma.reativar();
        assertTrue(turma.isAtiva());
    }

    @Test
    @DisplayName("recusa ano fora da faixa aceitavel")
    void recusaAnoInvalido() {
        assertThrows(RegraDeNegocioException.class,
                () -> Turma.nova("DS-01", TurmaTurnoEnum.MANHA, 1990, SemestreEnum.PRIMEIRO));
        assertThrows(RegraDeNegocioException.class,
                () -> Turma.nova("DS-01", TurmaTurnoEnum.MANHA,
                        LocalDate.now().getYear() + 5, SemestreEnum.PRIMEIRO));
    }

    @Test
    @DisplayName("atualizacao parcial preserva os campos nao informados")
    void atualizacaoParcial() {
        Turma turma = ativa();

        turma.atualizarDados("DS-02", null, null, null);

        assertEquals("DS-02", turma.getName());
        assertEquals(TurmaTurnoEnum.MANHA, turma.getShift());
        assertEquals(SemestreEnum.PRIMEIRO, turma.getSemester());
    }
}
