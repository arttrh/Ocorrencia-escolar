package br.com.project_sena.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.project_sena.application.core.domain.enums.SemestreEnum;
import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import br.com.project_sena.application.core.domain.enums.TurmaTurnoEnum;
import br.com.project_sena.application.core.domain.exception.AlunoInativoException;
import br.com.project_sena.application.core.domain.exception.AlunoJaVinculadoException;
import br.com.project_sena.application.core.domain.exception.TurmaCanceladaException;
import br.com.project_sena.application.core.domain.exception.TurmaCheiaException;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.core.domain.vo.PaginaRequest;
import br.com.project_sena.application.core.usecase.TurmaService;
import br.com.project_sena.application.core.usecase.validacoes.vinculo.ValidarAlunoAtivoParaVinculo;
import br.com.project_sena.application.core.usecase.validacoes.vinculo.ValidarAlunoSemOutraTurma;
import br.com.project_sena.application.core.usecase.validacoes.vinculo.ValidarTurmaComVaga;
import br.com.project_sena.application.port.in.command.CadastrarTurmaCommand;
import br.com.project_sena.application.port.in.command.VincularAlunoCommand;
import br.com.project_sena.fakes.FakesEmMemoria;

@DisplayName("TurmaService (caso de uso)")
class TurmaServiceTest {

    private FakesEmMemoria.TurmaRepositorioFake turmas;
    private FakesEmMemoria.AlunoRepositorioFake alunos;
    private FakesEmMemoria.VinculoRepositorioFake vinculos;
    private TurmaService service;
    private Turma turma;

    @BeforeEach
    void preparar() {
        turmas = new FakesEmMemoria.TurmaRepositorioFake();
        alunos = new FakesEmMemoria.AlunoRepositorioFake();
        vinculos = new FakesEmMemoria.VinculoRepositorioFake();
        service = new TurmaService(turmas, alunos, vinculos,
                List.of(new ValidarAlunoAtivoParaVinculo(),
                        new ValidarAlunoSemOutraTurma(vinculos),
                        new ValidarTurmaComVaga(vinculos)),
                FakesEmMemoria.transacaoDireta());

        turma = service.cadastrar(new CadastrarTurmaCommand(
                "DS-01", TurmaTurnoEnum.MANHA, LocalDate.now().getYear(), SemestreEnum.PRIMEIRO));
    }

    private Aluno novoAluno(String nome) {
        Aluno aluno = alunos.save(Aluno.novo(nome, LocalDate.of(2008, 1, 15)));
        vinculos.registrarAluno(aluno);
        return aluno;
    }

    @Test
    @DisplayName("matricula o aluno e ele aparece na lista da turma")
    void matriculaAluno() {
        Aluno ana = novoAluno("Ana");

        service.vincularAluno(new VincularAlunoCommand(ana.getId(), turma.getId()));

        assertEquals(List.of("Ana"),
                service.listarAlunos(turma.getId()).stream().map(Aluno::getName).toList());
    }

    @Test
    @DisplayName("recusa matricular o mesmo aluno duas vezes")
    void recusaMatriculaDuplicada() {
        Aluno ana = novoAluno("Ana");
        service.vincularAluno(new VincularAlunoCommand(ana.getId(), turma.getId()));

        assertThrows(AlunoJaVinculadoException.class, () -> service.vincularAluno(
                new VincularAlunoCommand(ana.getId(), turma.getId())));
    }

    @Test
    @DisplayName("recusa matricular aluno que ja esta em outra turma")
    void recusaAlunoDeOutraTurma() {
        Turma outra = service.cadastrar(new CadastrarTurmaCommand(
                "DS-02", TurmaTurnoEnum.TARDE, LocalDate.now().getYear(), SemestreEnum.PRIMEIRO));
        Aluno ana = novoAluno("Ana");
        service.vincularAluno(new VincularAlunoCommand(ana.getId(), turma.getId()));

        assertThrows(AlunoJaVinculadoException.class, () -> service.vincularAluno(
                new VincularAlunoCommand(ana.getId(), outra.getId())));
    }

    @Test
    @DisplayName("recusa matricular aluno inativo")
    void recusaAlunoInativo() {
        Aluno ana = novoAluno("Ana");
        ana.inativar();
        alunos.save(ana);

        assertThrows(AlunoInativoException.class, () -> service.vincularAluno(
                new VincularAlunoCommand(ana.getId(), turma.getId())));
    }

    @Test
    @DisplayName("recusa matricula em turma cancelada")
    void recusaTurmaCancelada() {
        service.cancelar(turma.getId());
        Aluno ana = novoAluno("Ana");

        assertThrows(TurmaCanceladaException.class, () -> service.vincularAluno(
                new VincularAlunoCommand(ana.getId(), turma.getId())));
    }

    @Test
    @DisplayName("recusa matricula quando a turma atinge a capacidade maxima")
    void recusaTurmaCheia() {
        for (int i = 0; i < Turma.CAPACIDADE_MAXIMA; i++) {
            Aluno aluno = novoAluno("Aluno " + i);
            service.vincularAluno(new VincularAlunoCommand(aluno.getId(), turma.getId()));
        }
        Aluno excedente = novoAluno("Excedente");

        assertThrows(TurmaCheiaException.class, () -> service.vincularAluno(
                new VincularAlunoCommand(excedente.getId(), turma.getId())));
    }

    @Test
    @DisplayName("cancelar move a turma da listagem de ativas para a de canceladas")
    void cancelaEListagemReflete() {
        service.cancelar(turma.getId());

        assertEquals(0, service.listar(TurmaEnum.ATIVA, PaginaRequest.padrao()).totalElementos());
        assertEquals(1, service.listar(TurmaEnum.CANCELADA, PaginaRequest.padrao()).totalElementos());
    }
}
