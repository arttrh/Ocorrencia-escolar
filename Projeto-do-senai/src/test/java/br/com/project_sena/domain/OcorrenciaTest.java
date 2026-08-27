package br.com.project_sena.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.enums.OcorrenciaEnum;
import br.com.project_sena.application.core.domain.enums.SemestreEnum;
import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import br.com.project_sena.application.core.domain.enums.TurmaTurnoEnum;
import br.com.project_sena.application.core.domain.exception.OcorrenciaCanceladaException;
import br.com.project_sena.application.core.domain.exception.RegraDeNegocioException;
import br.com.project_sena.application.core.domain.exception.TransicaoStatusInvalidaException;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.model.CategoriaOcorrencia;
import br.com.project_sena.application.core.domain.model.Ocorrencia;
import br.com.project_sena.application.core.domain.model.TipoOcorrencia;
import br.com.project_sena.application.core.domain.model.Turma;

/**
 * Regras da ocorrencia — testadas sem Spring, sem banco e sem mocks, o que so' e'
 * possivel porque o dominio ficou livre de framework.
 */
@DisplayName("Ocorrencia (dominio)")
class OcorrenciaTest {

    private static final Turma TURMA = new Turma(
            1L, "DS-01", TurmaTurnoEnum.MANHA, 2026, SemestreEnum.PRIMEIRO, TurmaEnum.ATIVA);
    private static final Aluno ALUNO = new Aluno(
            1L, "Ana Souza", LocalDate.of(2008, 3, 27), null, AlunoEnum.ATIVO);
    private static final CategoriaOcorrencia CATEGORIA =
            new CategoriaOcorrencia(1L, "DISCIPLINAR");
    private static final TipoOcorrencia TIPO =
            new TipoOcorrencia(1L, "INDISCIPLINA EM SALA", 1L);

    private static Ocorrencia nova() {
        return Ocorrencia.nova(TURMA, ALUNO, CATEGORIA, TIPO,
                LocalDateTime.now().minusHours(1), "Conversa excessiva durante a aula");
    }

    @Nested
    @DisplayName("ao ser criada")
    class AoSerCriada {

        @Test
        @DisplayName("nasce aguardando atendimento e nao cancelada")
        void nasceAguardando() {
            Ocorrencia ocorrencia = nova();

            assertEquals(OcorrenciaEnum.AGUARDANDO, ocorrencia.getStatus());
            assertFalse(ocorrencia.isDeleted());
        }

        @Test
        @DisplayName("assume o instante atual quando a data nao e' informada")
        void assumeAgoraQuandoSemData() {
            Ocorrencia ocorrencia = Ocorrencia.nova(
                    TURMA, ALUNO, CATEGORIA, TIPO, null, "Sem data informada");

            assertNotNull(ocorrencia.getRegisterDate());
        }

        @Test
        @DisplayName("recusa data de registro no futuro")
        void recusaDataFutura() {
            RegraDeNegocioException erro = assertThrows(RegraDeNegocioException.class,
                    () -> Ocorrencia.nova(TURMA, ALUNO, CATEGORIA, TIPO,
                            LocalDateTime.now().plusDays(1), "Ocorrencia do futuro"));

            assertTrue(erro.getMessage().contains("futuro"));
        }

        @Test
        @DisplayName("recusa descricao vazia")
        void recusaDescricaoVazia() {
            assertThrows(RegraDeNegocioException.class,
                    () -> Ocorrencia.nova(TURMA, ALUNO, CATEGORIA, TIPO, null, "   "));
        }

        @Test
        @DisplayName("recusa descricao acima do limite do banco")
        void recusaDescricaoLonga() {
            String longa = "x".repeat(Ocorrencia.TAMANHO_MAXIMO_DESCRICAO + 1);

            assertThrows(RegraDeNegocioException.class,
                    () -> Ocorrencia.nova(TURMA, ALUNO, CATEGORIA, TIPO, null, longa));
        }

        @Test
        @DisplayName("exige aluno, turma, categoria e tipo")
        void exigeRelacionamentos() {
            assertThrows(NullPointerException.class,
                    () -> Ocorrencia.nova(null, ALUNO, CATEGORIA, TIPO, null, "sem turma"));
            assertThrows(NullPointerException.class,
                    () -> Ocorrencia.nova(TURMA, null, CATEGORIA, TIPO, null, "sem aluno"));
        }
    }

    @Nested
    @DisplayName("maquina de estados do atendimento")
    class MaquinaDeEstados {

        @ParameterizedTest(name = "AGUARDANDO -> {0} e' permitido")
        @CsvSource({"ATENDENDO", "ATIVA", "FECHADA"})
        void transicoesValidasDeAguardando(OcorrenciaEnum destino) {
            Ocorrencia ocorrencia = nova();

            ocorrencia.alterarStatus(destino, LocalDateTime.now());

            assertEquals(destino, ocorrencia.getStatus());
            assertNotNull(ocorrencia.getUpdateDate());
        }

        @ParameterizedTest(name = "AGUARDANDO -> {0} e' recusado")
        @CsvSource({"RESOLVIDA", "NAO_RESOLVIDA"})
        void naoPulaDireitoParaConclusao(OcorrenciaEnum destino) {
            Ocorrencia ocorrencia = nova();

            assertThrows(TransicaoStatusInvalidaException.class,
                    () -> ocorrencia.alterarStatus(destino, LocalDateTime.now()));
        }

        @Test
        @DisplayName("recusa mudar para o mesmo status")
        void recusaMesmoStatus() {
            Ocorrencia ocorrencia = nova();

            assertThrows(TransicaoStatusInvalidaException.class,
                    () -> ocorrencia.alterarStatus(OcorrenciaEnum.AGUARDANDO, null));
        }

        @ParameterizedTest(name = "{0} e' um estado final")
        @EnumSource(value = OcorrenciaEnum.class,
                names = {"RESOLVIDA", "NAO_RESOLVIDA", "FECHADA"})
        void estadosFinaisNaoAceitamTransicao(OcorrenciaEnum finalizado) {
            Ocorrencia ocorrencia = nova();
            ocorrencia.alterarStatus(OcorrenciaEnum.ATENDENDO, null);
            ocorrencia.alterarStatus(finalizado, null);

            assertTrue(finalizado.isFinal());
            assertThrows(TransicaoStatusInvalidaException.class,
                    () -> ocorrencia.alterarStatus(OcorrenciaEnum.ATENDENDO, null));
        }

        @Test
        @DisplayName("percorre o fluxo completo de atendimento")
        void fluxoCompleto() {
            Ocorrencia ocorrencia = nova();

            ocorrencia.alterarStatus(OcorrenciaEnum.ATENDENDO, null);
            ocorrencia.alterarStatus(OcorrenciaEnum.RESOLVIDA, null);

            assertEquals(OcorrenciaEnum.RESOLVIDA, ocorrencia.getStatus());
        }
    }

    @Nested
    @DisplayName("exclusao logica")
    class ExclusaoLogica {

        @Test
        @DisplayName("marca como cancelada sem apagar o registro")
        void cancela() {
            Ocorrencia ocorrencia = nova();

            ocorrencia.cancelar();

            assertTrue(ocorrencia.isDeleted());
            assertNotNull(ocorrencia.getUpdateDate());
        }

        @Test
        @DisplayName("ocorrencia cancelada nao muda de status")
        void canceladaNaoMudaStatus() {
            Ocorrencia ocorrencia = nova();
            ocorrencia.cancelar();

            assertThrows(OcorrenciaCanceladaException.class,
                    () -> ocorrencia.alterarStatus(OcorrenciaEnum.ATENDENDO, null));
        }

        @Test
        @DisplayName("ocorrencia cancelada nao aceita edicao")
        void canceladaNaoAceitaEdicao() {
            Ocorrencia ocorrencia = nova();
            ocorrencia.cancelar();

            assertThrows(OcorrenciaCanceladaException.class,
                    () -> ocorrencia.atualizarDados(null, null, null, null, null, "nova descricao"));
        }

        @Test
        @DisplayName("nao cancela duas vezes")
        void naoCancelaDuasVezes() {
            Ocorrencia ocorrencia = nova();
            ocorrencia.cancelar();

            assertThrows(OcorrenciaCanceladaException.class, ocorrencia::cancelar);
        }
    }

    @Nested
    @DisplayName("atualizacao parcial")
    class AtualizacaoParcial {

        @Test
        @DisplayName("campos nulos preservam o valor atual")
        void preservaValoresAtuais() {
            Ocorrencia ocorrencia = nova();
            String descricaoOriginal = ocorrencia.getDescription();

            ocorrencia.atualizarDados(null, null, null, null, null, null);

            assertEquals(descricaoOriginal, ocorrencia.getDescription());
            assertEquals(TURMA, ocorrencia.getTurma());
        }

        @Test
        @DisplayName("descricao em branco nao apaga a existente")
        void descricaoEmBrancoNaoApaga() {
            Ocorrencia ocorrencia = nova();
            String original = ocorrencia.getDescription();

            ocorrencia.atualizarDados(null, null, null, null, null, "   ");

            assertEquals(original, ocorrencia.getDescription());
        }

        @Test
        @DisplayName("registra a data da ultima alteracao")
        void registraDataDeAlteracao() {
            Ocorrencia ocorrencia = nova();

            ocorrencia.atualizarDados(null, null, null, null, null, "Descricao revisada");

            assertEquals("Descricao revisada", ocorrencia.getDescription());
            assertNotNull(ocorrencia.getUpdateDate());
        }
    }

    @Nested
    @DisplayName("mapeamento de status")
    class MapeamentoDeStatus {

        @ParameterizedTest(name = "\"{0}\" resolve para {1}")
        @CsvSource({
                "waiting, AGUARDANDO",
                "progressing, ATENDENDO",
                "active, ATIVA",
                "solved, RESOLVIDA",
                "unsolved, NAO_RESOLVIDA",
                "closed, FECHADA",
                "AGUARDANDO, AGUARDANDO",
                "aguardando, AGUARDANDO"})
        void aceitaSlugEnome(String entrada, OcorrenciaEnum esperado) {
            assertEquals(esperado, OcorrenciaEnum.porSlugOuNome(entrada));
        }

        @Test
        @DisplayName("valor desconhecido devolve nulo em vez de estourar")
        void valorDesconhecido() {
            org.junit.jupiter.api.Assertions.assertNull(OcorrenciaEnum.porSlugOuNome("inexistente"));
            org.junit.jupiter.api.Assertions.assertNull(OcorrenciaEnum.porSlugOuNome(null));
        }
    }
}
