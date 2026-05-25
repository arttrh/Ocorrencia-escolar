package br.com.project_sena.application.core.usecase;

import br.com.project_sena.application.port.out.AlunoRepository;
import org.springframework.stereotype.Service;

@Service
public class AlunoService {

    private AlunoRepository alunoRepository;

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }


}
