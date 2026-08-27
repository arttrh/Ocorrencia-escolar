package br.com.project_sena.application.core.usecase.validacoes.vinculo;

import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.model.Turma;

/** Uma regra de validacao da matricula de um aluno em uma turma. */
@FunctionalInterface
public interface ValidadorVinculo {

    void validar(Aluno aluno, Turma turma);
}
