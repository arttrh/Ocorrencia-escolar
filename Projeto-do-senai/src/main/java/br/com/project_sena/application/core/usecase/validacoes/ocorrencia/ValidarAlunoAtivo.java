package br.com.project_sena.application.core.usecase.validacoes.ocorrencia;

import br.com.project_sena.application.core.domain.exception.AlunoInativoException;

public class ValidarAlunoAtivo implements ValidadorOcorrencia {

    @Override
    public void validar(ContextoOcorrencia contexto) {
        if (!contexto.aluno().isAtivo()) {
            throw new AlunoInativoException(
                    "Aluno inativo nao pode receber ocorrencias: " + contexto.aluno().getName());
        }
    }
}
