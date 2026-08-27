package br.com.project_sena.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.enums.OcorrenciaEnum;
import br.com.project_sena.application.core.domain.enums.SemestreEnum;
import br.com.project_sena.application.core.domain.enums.TurmaTurnoEnum;
import br.com.project_sena.application.core.domain.exception.AlunoInativoException;
import br.com.project_sena.application.core.domain.exception.CategoriaNotFoundException;
import br.com.project_sena.application.core.domain.exception.OcorrenciaNotFoundException;
import br.com.project_sena.application.core.domain.exception.RegraDeNegocioException;
import br.com.project_sena.application.core.domain.exception.TurmaCanceladaException;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.model.CategoriaOcorrencia;
import br.com.project_sena.application.core.domain.model.Ocorrencia;
import br.com.project_sena.application.core.domain.model.TipoOcorrencia;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.core.domain.vo.PaginaRequest;
import br.com.project_sena.application.core.domain.vo.ResumoOcorrencias;
import br.com.project_sena.application.core.usecase.OcorrenciaService;
import br.com.project_sena.application.core.usecase.validacoes.ocorrencia.ValidarAlunoAtivo;
import br.com.project_sena.application.core.usecase.validacoes.ocorrencia.ValidarAlunoPertenceATurma;
import br.com.project_sena.application.core.usecase.validacoes.ocorrencia.ValidarTurmaAtiva;
import br.com.project_sena.application.port.in.command.AlterarStatusOcorrenciaCommand;
import br.com.project_sena.application.port.in.command.AtualizarOcorrenciaCommand;
import br.com.project_sena.application.port.in.command.CadastrarOcorrenciaCommand;
import br.com.project_sena.fakes.FakesEmMemoria;

@DisplayName("OcorrenciaService (caso de uso)")
class OcorrenciaServiceTest {

    private FakesEmMemoria.OcorrenciaRepositorioFake ocorrencias;
    private FakesEmMemoria.TurmaRepositorioFake turmas;
    private FakesEmMemoria.AlunoRepositorioFake alunos;
    private FakesEmMemoria.CategoriaRepositorioFake categorias;
    private FakesEmMemoria.TipoRepositorioFake tipos;
    private FakesEmMemoria.VinculoRepositorioFake vinculos;
    private FakesEmMemoria.EventosCapturados eventos;
    private OcorrenciaService service;

    private Turma turma;
    private Aluno aluno;

    @BeforeEach
    void preparar() {
        ocorrencias = new FakesEmMemoria.OcorrenciaRepositorioFake();
        turmas = new FakesEmMemoria.TurmaRepositorioFake();
        alunos = new FakesEmMemoria.AlunoRepositorioFake();
        categorias = new FakesEmMemoria.CategoriaRepositorioFake();
        tipos = new FakesEmMemoria.TipoRepositorioFake();
        vinculos = new FakesEmMemoria.VinculoRepositorioFake();
        eventos = new FakesEmMemoria.EventosCapturados();

        service = new OcorrenciaService(
                ocorrencias, turmas, alunos, categorias, tipos,
                List.of(new ValidarAlunoAtivo(),
                        new ValidarTurmaAtiva(),
                        new ValidarAlunoPertenceATurma(vinculos)),
                eventos,
                FakesEmMemoria.transacaoDireta());

        turma = turmas.save(Turma.nova("DS-01", TurmaTurnoEnum.MANHA,
                LocalDate.now().getYear(), SemestreEnum.PRIMEIRO));
        aluno = alunos.save(Aluno.novo("Ana Souza", LocalDate.of(2008, 3, 27)));
        vinculos.registrarAluno(aluno);
        vinculos.vincular(aluno.getId(), turma.getId());

        CategoriaOcorrencia categoria = categorias.save(new CategoriaOcorrencia(null, "DISCIPLINAR"));
        tipos.save(new TipoOcorrencia(null, "INDISCIPLINA EM SALA", categoria.getId()));

        CategoriaOcorrencia outra = categorias.save(new CategoriaOcorrencia(null, "FREQUENCIA"));
        tipos.save(new TipoOcorrencia(null, "FALTA NAO JUSTIFICADA", outra.getId()));
    }

    private CadastrarOcorrenciaCommand comando() {
        return new CadastrarOcorrenciaCommand(turma.getId(), aluno.getId(),
                "DISCIPLINAR", "INDISCIPLINA EM SALA",
                LocalDateTime.now().minusHours(2), "Conversa excessiva durante a aula");
    }

    @Test
    @DisplayName("registra a ocorrencia e publica o evento")
    void registraEPublica() {
        Ocorrencia salva = service.cadastrar(comando());

        assertEquals(OcorrenciaEnum.AGUARDANDO, salva.getStatus());
        assertEquals(aluno.getId(), salva.getStudent().getId());
        assertEquals(1, eventos.registradas.size());
    }

    @Test
    @DisplayName("recusa aluno que nao esta matriculado na turma informada")
    void recusaAlunoDeOutraTurma() {
        Aluno forasteiro = alunos.save(Aluno.novo("Bruno Lima", LocalDate.of(2007, 5, 10)));

        RegraDeNegocioException erro = assertThrows(RegraDeNegocioException.class,
                () -> service.cadastrar(new CadastrarOcorrenciaCommand(
                        turma.getId(), forasteiro.getId(), "DISCIPLINAR",
                        "INDISCIPLINA EM SALA", null, "Fato qualquer")));

        assertTrue(erro.getMessage().contains("nao esta matriculado"));
    }

    @Test
    @DisplayName("recusa aluno inativo")
    void recusaAlunoInativo() {
        Aluno inativo = new Aluno(aluno.getId(), aluno.getName(), aluno.getBirthDate(),
                null, AlunoEnum.INATIVO);
        alunos.save(inativo);

        assertThrows(AlunoInativoException.class, () -> service.cadastrar(comando()));
    }

    @Test
    @DisplayName("recusa turma cancelada")
    void recusaTurmaCancelada() {
        turma.cancelar();
        turmas.save(turma);

        assertThrows(TurmaCanceladaException.class, () -> service.cadastrar(comando()));
    }

    @Test
    @DisplayName("recusa categoria inexistente")
    void recusaCategoriaInexistente() {
        assertThrows(CategoriaNotFoundException.class,
                () -> service.cadastrar(new CadastrarOcorrenciaCommand(
                        turma.getId(), aluno.getId(), "INVENTADA", "INDISCIPLINA EM SALA",
                        null, "Fato qualquer")));
    }

    @Test
    @DisplayName("recusa tipo que pertence a outra categoria")
    void recusaTipoDeOutraCategoria() {
        RegraDeNegocioException erro = assertThrows(RegraDeNegocioException.class,
                () -> service.cadastrar(new CadastrarOcorrenciaCommand(
                        turma.getId(), aluno.getId(), "DISCIPLINAR", "FALTA NAO JUSTIFICADA",
                        null, "Tipo trocado")));

        assertTrue(erro.getMessage().contains("nao pertence a categoria"));
    }

    @Test
    @DisplayName("muda o status seguindo a maquina de estados e publica o evento")
    void mudaStatus() {
        Ocorrencia salva = service.cadastrar(comando());

        Ocorrencia atendendo = service.alterarStatus(new AlterarStatusOcorrenciaCommand(
                salva.getId(), "progressing", LocalDateTime.now()));

        assertEquals(OcorrenciaEnum.ATENDENDO, atendendo.getStatus());
        assertEquals(1, eventos.statusAlterados.size());
    }

    @Test
    @DisplayName("recusa status desconhecido")
    void recusaStatusDesconhecido() {
        Ocorrencia salva = service.cadastrar(comando());

        assertThrows(RegraDeNegocioException.class,
                () -> service.alterarStatus(new AlterarStatusOcorrenciaCommand(
                        salva.getId(), "inexistente", null)));
    }

    @Test
    @DisplayName("atualizacao parcial nao apaga os campos omitidos")
    void atualizacaoParcial() {
        Ocorrencia salva = service.cadastrar(comando());

        Ocorrencia atualizada = service.atualizar(new AtualizarOcorrenciaCommand(
                salva.getId(), null, null, null, null, null, "Descricao revisada"));

        assertEquals("Descricao revisada", atualizada.getDescription());
        assertEquals(salva.getTurma().getId(), atualizada.getTurma().getId());
        assertEquals(salva.getCategory().getName(), atualizada.getCategory().getName());
    }

    @Test
    @DisplayName("ocorrencia cancelada some da listagem mas continua consultavel por id")
    void cancelamentoEhLogico() {
        Ocorrencia salva = service.cadastrar(comando());

        service.cancelar(salva.getId());

        assertEquals(0, service.listar(PaginaRequest.padrao()).totalElementos());
        assertTrue(service.buscar(salva.getId()).isDeleted());
    }

    @Test
    @DisplayName("buscar id inexistente devolve erro de recurso nao encontrado")
    void buscarInexistente() {
        assertThrows(OcorrenciaNotFoundException.class, () -> service.buscar(999L));
    }

    @Test
    @DisplayName("historico do aluno vem da mais recente para a mais antiga")
    void historicoOrdenado() {
        service.cadastrar(new CadastrarOcorrenciaCommand(turma.getId(), aluno.getId(),
                "DISCIPLINAR", "INDISCIPLINA EM SALA",
                LocalDateTime.now().minusDays(5), "Fato antigo"));
        service.cadastrar(new CadastrarOcorrenciaCommand(turma.getId(), aluno.getId(),
                "DISCIPLINAR", "INDISCIPLINA EM SALA",
                LocalDateTime.now().minusHours(1), "Fato recente"));

        List<Ocorrencia> historico = service.historicoDoAluno(aluno.getId());

        assertEquals(2, historico.size());
        assertEquals("Fato recente", historico.get(0).getDescription());
    }

    @Test
    @DisplayName("resumo conta por situacao e agrega por categoria")
    void resumoDoDashboard() {
        Ocorrencia primeira = service.cadastrar(comando());
        service.cadastrar(comando());
        service.alterarStatus(new AlterarStatusOcorrenciaCommand(
                primeira.getId(), "progressing", null));

        ResumoOcorrencias resumo = service.resumo();

        assertEquals(1, resumo.total(OcorrenciaEnum.ATENDENDO));
        assertEquals(1, resumo.total(OcorrenciaEnum.AGUARDANDO));
        assertEquals(2, resumo.totalGeral());
        assertEquals("DISCIPLINAR", resumo.byCategory().get(0).key());
        assertEquals(2, resumo.byCategory().get(0).value());
    }

    @Test
    @DisplayName("lista os tipos de uma categoria")
    void listaTiposDaCategoria() {
        List<TipoOcorrencia> tiposDisciplinar = service.listarTiposPorCategoria("DISCIPLINAR");

        assertEquals(1, tiposDisciplinar.size());
        assertEquals("INDISCIPLINA EM SALA", tiposDisciplinar.get(0).getName());
    }
}
