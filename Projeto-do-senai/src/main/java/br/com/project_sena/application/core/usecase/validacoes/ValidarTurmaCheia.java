package br.com.project_sena.application.core.usecase.validacoes;

import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.exception.type.Turma.TurmaCheiaException;
import org.springframework.stereotype.Component;

@Component
public class ValidarTurmaCheia{

    
    public void validar(Aluno aluno, Turma turma) {
        if (turma.getAluno().size() >= turma.getTurmaCheia()){
            throw new TurmaCheiaException("Turma ja esta lotada nao pode cadastrar usuario");
        }
    }
}
