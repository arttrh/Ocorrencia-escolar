package br.com.project_sena.application.core.usecase;

import br.com.project_sena.application.port.out.AlunoRepository;
import org.springframework.stereotype.Service;

@Service
public class AlunoService {

    private AlunoRepository alunoRepository;

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    public Page<Aluno> listar(Pageable paginacao){
        Aluno aluno = alunoRepository.fidAll(paginacao)
        return aluno;
    }

    public Aluno atualizar(Long id){
        Aluno aluno = alunoRepository.findById(id).orElseThrow(() -> new RunTimeException(""));
        Aluno alunoAtualizado = aluno.atualizarAluno();
        alunoRepository.save(alunoAtualizado);
        return alunoAtualizado;
    }

    public void excluir(Long id){
        Aluno aluno = alunoRepository.findById(id).orElseThrow(() -> new RunTimeException(""));
        aluno.excluir();
        alunoRepository.save(aluno);
    }
    
    public void reativar(Long id){
        Aluno aluno = alunoRepository.findById(id).orElseThrow(() -> new RunTimeException(""));
        aluno.reativar();
        alunoRepository.save(aluno);
    }

}
