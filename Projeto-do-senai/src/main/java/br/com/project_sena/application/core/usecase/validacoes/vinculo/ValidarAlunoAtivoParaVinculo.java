package br.com.project_sena.application.core.usecase.validacoes.vinculo;

import br.com.project_sena.application.core.domain.exception.AlunoInativoException;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.model.Turma;

public class ValidarAlunoAtivoParaVinculo implements ValidadorVinculo {

    @Override
    public void validar(Aluno aluno, Turma turma) {
        if (!aluno.isAtivo()) {
            throw new AlunoInativoException("Aluno inativo nao pode ser vinculado a uma turma");
        }
    }
}
