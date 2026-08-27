package br.com.project_sena.application.core.usecase;

import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.exception.AlunoNotFoundException;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.vo.Pagina;
import br.com.project_sena.application.core.domain.vo.PaginaRequest;
import br.com.project_sena.application.port.in.AlunoUseCase;
import br.com.project_sena.application.port.in.command.AlterarFotoAlunoCommand;
import br.com.project_sena.application.port.in.command.AtualizarAlunoCommand;
import br.com.project_sena.application.port.in.command.CadastrarAlunoCommand;
import br.com.project_sena.application.port.out.AlunoRepository;
import br.com.project_sena.application.port.out.TransacaoPort;

public class AlunoService implements AlunoUseCase {

    private final AlunoRepository alunoRepository;
    private final TransacaoPort transacao;

    public AlunoService(AlunoRepository alunoRepository, TransacaoPort transacao) {
        this.alunoRepository = alunoRepository;
        this.transacao = transacao;
    }

    @Override
    public Aluno cadastrar(CadastrarAlunoCommand command) {
        return transacao.executar(
                () -> alunoRepository.save(Aluno.novo(command.name(), command.birthDate())));
    }

    @Override
    public Aluno buscar(Long id) {
        return alunoRepository.findById(id)
                .orElseThrow(() -> new AlunoNotFoundException("Aluno nao encontrado: " + id));
    }

    @Override
    public Pagina<Aluno> listar(AlunoEnum status, PaginaRequest paginaRequest) {
        return alunoRepository.findByStatus(status, paginaRequest);
    }

    /**
     * A versao anterior carregava o aluno do banco, ignorava o resultado e salvava o
     * objeto vindo do DTO — o que zerava a foto e o status a cada atualizacao. Aqui a
     * alteracao e' aplicada sobre a entidade carregada.
     */
    @Override
    public Aluno atualizar(AtualizarAlunoCommand command) {
        return transacao.executar(() -> {
            Aluno aluno = buscar(command.id());
            aluno.atualizarDados(command.name(), command.birthDate());
            return alunoRepository.save(aluno);
        });
    }

    @Override
    public Aluno alterarFoto(AlterarFotoAlunoCommand command) {
        return transacao.executar(() -> {
            Aluno aluno = buscar(command.id());
            aluno.alterarFoto(command.imageUrl());
            return alunoRepository.save(aluno);
        });
    }

    @Override
    public void inativar(Long id) {
        transacao.executar(() -> {
            Aluno aluno = buscar(id);
            aluno.inativar();
            return alunoRepository.save(aluno);
        });
    }

    @Override
    public Aluno reativar(Long id) {
        return transacao.executar(() -> {
            Aluno aluno = buscar(id);
            aluno.reativar();
            return alunoRepository.save(aluno);
        });
    }
}
