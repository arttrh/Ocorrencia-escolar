package br.com.project_sena.application.core.usecase;

import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.port.out.AlunoRepository;
import br.com.project_sena.application.port.out.TurmaRepository;
import br.com.project_sena.exception.type.AlunoNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

@Service
public class TurmaService {

    private final TurmaRepository repository;

    public TurmaService(TurmaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Turma cadastrar(Turma dados) {
        Turma saved = repository.save(dados);
        return saved;
    }
}