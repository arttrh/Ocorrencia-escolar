package br.com.project_sena.bdd;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.com.project_sena.application.core.domain.enums.*;
import br.com.project_sena.application.core.domain.model.*;
import br.com.project_sena.application.core.usecase.validacoes.ValidarVinculo;
import br.com.project_sena.application.port.out.TurmaRepository;
import io.cucumber.java.pt.*;
import java.time.*;
public class VinculoSteps {
    private TurmaRepository repository;
    private RuntimeException erro;
    @Dado("um aluno ativo sem turma")
    public void semTurma() {
        repository = mock(TurmaRepository.class);
    }
    @Dado("um aluno ativo já vinculado")
    public void comTurma() {
        semTurma();
        when(repository.alunoTemTurma(1L)).thenReturn(true);
    }
    @Quando("vinculo o aluno a uma turma ativa com vaga")
    public void vincular() {
        try {
            new ValidarVinculo(repository)
                .validarTurmaEAluno(new Aluno(1L, "foto", "Aluno", LocalDate.of(2010, 1, 1), AlunoEnum.ATIVO),
                    new Turma(2L, "Turma", TurmaTurnoEnum.MANHA, LocalDateTime.now(), TurmaEnum.ATIVA,
                        LocalDateTime.now()));
        } catch (RuntimeException ex) {
            erro = ex;
        }
    }
    @Então("o vínculo é permitido")
    public void permitido() {
        assertNull(erro);
    }
    @Então("o vínculo é rejeitado")
    public void rejeitado() {
        assertInstanceOf(IllegalArgumentException.class, erro);
    }
}
