package br.com.project_sena.application.core.usecase;

import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.port.out.AlunoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AlunoService {

    private AlunoRepository alunoRepository;

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    public Aluno cadastrar(Aluno dados){
       Aluno saved = alunoRepository.save(dados);
       return saved;
    }

    public Aluno buscar(Long id){
        Aluno aluno = alunoRepository.findById(id).orElseThrow(() -> new RuntimeException(""));//Completar
        return aluno;
    }

    public Page<Aluno> listarAtivos(Pageable paginacao){
        return alunoRepository.findByAlunoEnum(paginacao, AlunoEnum.ATIVO);
    }

    public Page<Aluno> listarInativos(Pageable paginacao){
        return alunoRepository.findByAlunoEnum(paginacao, AlunoEnum.INVATIVO);
    }

    public Aluno atualizar(Long id, Aluno aluno){
        Aluno alunoBuscar = alunoRepository.findById(id).orElseThrow(() -> new RuntimeException(""));
        aluno.atualizarAluno(aluno);
        return alunoRepository.save(aluno);
    }

    public void excluir(Long id){
        Aluno aluno = alunoRepository.findById(id).orElseThrow(() -> new RuntimeException(""));
        aluno.excluir();
        alunoRepository.save(aluno);
    }

    public void reativar(Long id){
        Aluno aluno = alunoRepository.findById(id).orElseThrow(() -> new RuntimeException(""));
        aluno.reativar();
        alunoRepository.save(aluno);
    }
}
