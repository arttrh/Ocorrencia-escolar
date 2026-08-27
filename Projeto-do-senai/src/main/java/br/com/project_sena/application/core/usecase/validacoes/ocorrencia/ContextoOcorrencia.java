package br.com.project_sena.application.core.usecase.validacoes.ocorrencia;

import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.model.Turma;

/** Dados ja resolvidos que os validadores de ocorrencia inspecionam. */
public record ContextoOcorrencia(Aluno aluno, Turma turma) {
}
