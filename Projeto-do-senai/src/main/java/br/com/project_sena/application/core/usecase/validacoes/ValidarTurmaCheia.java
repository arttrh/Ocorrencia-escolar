package br.com.project_sena.application.core.usecase.validacoes;

import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.exception.type.Turma.TurmaCheiaException;
import org.springframework.stereotype.Component;

@Component
public class ValidarTurmaCheia {

    public void turmaCheia(Turma dados){
        if (dados.getAluno().size() >= dados.getTurmaCheia()){
            throw new TurmaCheiaException("Turma ja esta lotada nao pode cadastrar usuario");
        }
    }
}
