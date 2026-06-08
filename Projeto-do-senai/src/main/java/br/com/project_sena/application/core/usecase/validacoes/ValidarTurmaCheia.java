package br.com.project_sena.application.core.usecase.validacoes;

import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.exception.type.Turma.TurmaCheiaException;

public class ValidarTurmaCheia {

    public void validarTurmaCheia(Turma dados){
        if (dados.getAluno().size() >= dados.getTurmaCheia()){
            throw new TurmaCheiaException("Turma ja esta lotada nao pode cadastrar usuario");
        }
    }
}
