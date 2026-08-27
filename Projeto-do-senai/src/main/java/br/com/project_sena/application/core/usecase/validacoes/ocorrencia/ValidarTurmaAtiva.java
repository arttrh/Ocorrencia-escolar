package br.com.project_sena.application.core.usecase.validacoes.ocorrencia;

import br.com.project_sena.application.core.domain.exception.TurmaCanceladaException;

public class ValidarTurmaAtiva implements ValidadorOcorrencia {

    @Override
    public void validar(ContextoOcorrencia contexto) {
        if (contexto.turma().isCancelada()) {
            throw new TurmaCanceladaException(
                    "Turma cancelada nao aceita novas ocorrencias: " + contexto.turma().getName());
        }
    }
}
