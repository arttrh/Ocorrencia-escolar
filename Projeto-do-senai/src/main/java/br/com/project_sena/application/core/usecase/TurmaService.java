package br.com.project_sena.application.core.usecase;

import java.util.List;

import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import br.com.project_sena.application.core.domain.exception.TurmaNotFoundException;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.core.domain.vo.Pagina;
import br.com.project_sena.application.core.domain.vo.PaginaRequest;
import br.com.project_sena.application.core.usecase.validacoes.vinculo.ValidadorVinculo;
import br.com.project_sena.application.port.in.TurmaUseCase;
import br.com.project_sena.application.port.in.command.AtualizarTurmaCommand;
import br.com.project_sena.application.port.in.command.CadastrarTurmaCommand;
import br.com.project_sena.application.port.in.command.VincularAlunoCommand;
import br.com.project_sena.application.port.out.AlunoRepository;
import br.com.project_sena.application.port.out.TransacaoPort;
import br.com.project_sena.application.port.out.TurmaRepository;
import br.com.project_sena.application.port.out.VinculoRepository;
import br.com.project_sena.application.core.domain.exception.AlunoNotFoundException;

public class TurmaService implements TurmaUseCase {

    private final TurmaRepository turmaRepository;
    private final AlunoRepository alunoRepository;
    private final VinculoRepository vinculoRepository;
    private final List<ValidadorVinculo> validadoresDeVinculo;
    private final TransacaoPort transacao;

    public TurmaService(TurmaRepository turmaRepository,
                        AlunoRepository alunoRepository,
                        VinculoRepository vinculoRepository,
                        List<ValidadorVinculo> validadoresDeVinculo,
                        TransacaoPort transacao) {
        this.turmaRepository = turmaRepository;
        this.alunoRepository = alunoRepository;
        this.vinculoRepository = vinculoRepository;
        this.validadoresDeVinculo = List.copyOf(validadoresDeVinculo);
        this.transacao = transacao;
    }

    @Override
    public Turma cadastrar(CadastrarTurmaCommand command) {
        Turma turma = Turma.nova(
                command.name(), command.shift(), command.year(), command.semester());
        return transacao.executar(() -> turmaRepository.save(turma));
    }

    @Override
    public Turma buscar(Long id) {
        return turmaRepository.findById(id)
                .orElseThrow(() -> new TurmaNotFoundException("Turma nao encontrada: " + id));
    }

    @Override
    public Pagina<Turma> listar(TurmaEnum status, PaginaRequest paginaRequest) {
        return turmaRepository.findByStatus(status, paginaRequest);
    }

    @Override
    public Turma atualizar(AtualizarTurmaCommand command) {
        return transacao.executar(() -> {
            Turma turma = buscar(command.id());
            turma.atualizarDados(
                    command.name(), command.shift(), command.year(), command.semester());
            return turmaRepository.save(turma);
        });
    }

    @Override
    public void cancelar(Long id) {
        transacao.executar(() -> {
            Turma turma = buscar(id);
            turma.cancelar();
            return turmaRepository.save(turma);
        });
    }

    @Override
    public Turma reativar(Long id) {
        return transacao.executar(() -> {
            Turma turma = buscar(id);
            turma.reativar();
            return turmaRepository.save(turma);
        });
    }

    /**
     * Matricula um aluno em uma turma aplicando toda a cadeia de validadores.
     *
     * <p>Roda em transacao porque validar (contar matriculas) e gravar o vinculo precisam
     * ser atomicos: sem isso, duas matriculas simultaneas poderiam estourar a capacidade.</p>
     */
    @Override
    public void vincularAluno(VincularAlunoCommand command) {
        transacao.executar(() -> {
            Aluno aluno = alunoRepository.findById(command.alunoId())
                    .orElseThrow(() -> new AlunoNotFoundException(
                            "Aluno nao encontrado: " + command.alunoId()));
            Turma turma = buscar(command.turmaId());

            validadoresDeVinculo.forEach(validador -> validador.validar(aluno, turma));

            vinculoRepository.vincular(aluno.getId(), turma.getId());
            return null;
        });
    }

    @Override
    public List<Aluno> listarAlunos(Long turmaId) {
        buscar(turmaId);
        return vinculoRepository.listarAlunosDaTurma(turmaId);
    }
}
