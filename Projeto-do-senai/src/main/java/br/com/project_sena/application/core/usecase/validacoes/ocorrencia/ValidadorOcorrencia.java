package br.com.project_sena.application.core.usecase.validacoes.ocorrencia;

/**
 * Uma regra de validacao de ocorrencia.
 *
 * <p>Substitui as classes {@code Validar} / {@code ValidarTurmaEAlunoEOcorrencia} /
 * {@code ValidarOcorrencia}, que misturavam validar, persistir e imprimir no console.
 * Cada regra agora e' uma implementacao independente, testavel isoladamente e
 * plugavel na lista sem alterar o use case.</p>
 */
@FunctionalInterface
public interface ValidadorOcorrencia {

    void validar(ContextoOcorrencia contexto);
}
