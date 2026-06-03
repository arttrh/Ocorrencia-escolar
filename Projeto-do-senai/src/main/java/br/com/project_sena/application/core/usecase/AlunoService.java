package br.com.project_sena.application.core.usecase;

import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.port.out.AlunoRepository;
import br.com.project_sena.exception.type.AlunoExistingException;
import br.com.project_sena.exception.type.AlunoNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AlunoService {

    private AlunoRepository alunoRepository;

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    @Transactional
    public Aluno cadastrar(Aluno dados){
       Aluno saved = alunoRepository.save(dados);
       return saved;
    }

    public Aluno buscar(Long id){
        Aluno aluno = alunoRepository.findById(id).orElseThrow(() -> new RuntimeException("Aluno nao encontrado"));
        return aluno;
    }

    public Page<Aluno> listarAtivos(Pageable paginacao){
        return alunoRepository.findByAlunoEnum(paginacao, AlunoEnum.ATIVO);
    }

    public Page<Aluno> listarInativos(Pageable paginacao){
        return alunoRepository.findByAlunoEnum(paginacao, AlunoEnum.INVATIVO);
    }

    @Transactional
    public Aluno atualizar(Long id, Aluno aluno){
        Aluno alunoBuscar = alunoRepository.findById(id).orElseThrow(() -> new AlunoNotFoundException("Aluno nao encontrado"));
        aluno.atualizarAluno(
                aluno.getPhoto(),
                aluno.getName(),
                aluno.getDateBirth()
        );
        return alunoRepository.save(aluno);
    }

    public void excluir(Long id){
        Aluno aluno = alunoRepository.findById(id).orElseThrow(() -> new AlunoNotFoundException("Aluno nao Encontrado"));
        aluno.excluir();
        alunoRepository.save(aluno);
    }

    public Aluno reativar(Long id){
        Aluno aluno = alunoRepository.findById(id).orElseThrow(() -> new AlunoNotFoundException("Aluno nao encontrado"));
        if (aluno.getAlunoEnum() == AlunoEnum.ATIVO){
            throw new AlunoExistingException("Aluno ja esta Ativo");
        }
        aluno.reativar();
        return alunoRepository.save(aluno);
    }
}
