package br.com.project_sena.application.core.usecase;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import br.com.project_sena.application.core.domain.enums.OcorrenciaEnum;
import br.com.project_sena.application.core.domain.exception.AlunoNotFoundException;
import br.com.project_sena.application.core.domain.exception.CategoriaNotFoundException;
import br.com.project_sena.application.core.domain.exception.OcorrenciaNotFoundException;
import br.com.project_sena.application.core.domain.exception.RegraDeNegocioException;
import br.com.project_sena.application.core.domain.exception.TipoOcorrenciaNotFoundException;
import br.com.project_sena.application.core.domain.exception.TurmaNotFoundException;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.model.CategoriaOcorrencia;
import br.com.project_sena.application.core.domain.model.Ocorrencia;
import br.com.project_sena.application.core.domain.model.TipoOcorrencia;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.core.domain.vo.Pagina;
import br.com.project_sena.application.core.domain.vo.PaginaRequest;
import br.com.project_sena.application.core.domain.vo.ResumoOcorrencias;
import br.com.project_sena.application.core.usecase.validacoes.ocorrencia.ContextoOcorrencia;
import br.com.project_sena.application.core.usecase.validacoes.ocorrencia.ValidadorOcorrencia;
import br.com.project_sena.application.port.in.OcorrenciaUseCase;
import br.com.project_sena.application.port.in.command.AlterarStatusOcorrenciaCommand;
import br.com.project_sena.application.port.in.command.AtualizarOcorrenciaCommand;
import br.com.project_sena.application.port.in.command.CadastrarOcorrenciaCommand;
import br.com.project_sena.application.port.out.AlunoRepository;
import br.com.project_sena.application.port.out.CategoriaOcorrenciaRepository;
import br.com.project_sena.application.port.out.EventoOcorrenciaPort;
import br.com.project_sena.application.port.out.OcorrenciaRepository;
import br.com.project_sena.application.port.out.TipoOcorrenciaRepository;
import br.com.project_sena.application.port.out.TransacaoPort;
import br.com.project_sena.application.port.out.TurmaRepository;

/**
 * Use case completo de ocorrencias: cadastro, consulta, atualizacao, mudanca de status,
 * cancelamento logico, historico por aluno e o resumo do dashboard.
 *
 * <p>Substitui o {@code OcorrenciaService} anterior, que tinha um unico metodo chamando
 * assinaturas inexistentes ({@code validarOcorrencia.validar(aluno, turma)}) e delegava a
 * persistencia para dentro de um validador.</p>
 */
public class OcorrenciaService implements OcorrenciaUseCase {

    private final OcorrenciaRepository ocorrenciaRepository;
    private final TurmaRepository turmaRepository;
    private final AlunoRepository alunoRepository;
    private final CategoriaOcorrenciaRepository categoriaRepository;
    private final TipoOcorrenciaRepository tipoRepository;
    private final List<ValidadorOcorrencia> validadores;
    private final EventoOcorrenciaPort eventos;
    private final TransacaoPort transacao;

    public OcorrenciaService(OcorrenciaRepository ocorrenciaRepository,
                             TurmaRepository turmaRepository,
                             AlunoRepository alunoRepository,
                             CategoriaOcorrenciaRepository categoriaRepository,
                             TipoOcorrenciaRepository tipoRepository,
                             List<ValidadorOcorrencia> validadores,
                             EventoOcorrenciaPort eventos,
                             TransacaoPort transacao) {
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.turmaRepository = turmaRepository;
        this.alunoRepository = alunoRepository;
        this.categoriaRepository = categoriaRepository;
        this.tipoRepository = tipoRepository;
        this.validadores = List.copyOf(validadores);
        this.eventos = eventos;
        this.transacao = transacao;
    }

    @Override
    public Ocorrencia cadastrar(CadastrarOcorrenciaCommand command) {
        Ocorrencia salva = transacao.executar(() -> {
            Turma turma = exigirTurma(command.schoolClassId());
            Aluno aluno = exigirAluno(command.studentId());
            CategoriaOcorrencia categoria = exigirCategoria(command.category());
            TipoOcorrencia tipo = exigirTipoDaCategoria(command.type(), categoria);

            validar(aluno, turma);

            Ocorrencia ocorrencia = Ocorrencia.nova(
                    turma, aluno, categoria, tipo, command.registerDate(), command.description());
            return ocorrenciaRepository.save(ocorrencia);
        });

        // Publicado fora da transacao: um broker indisponivel nao deve desfazer o registro.
        eventos.ocorrenciaRegistrada(salva);
        return salva;
    }

    @Override
    public Ocorrencia buscar(Long id) {
        return ocorrenciaRepository.findById(id)
                .orElseThrow(() -> new OcorrenciaNotFoundException("Ocorrencia nao encontrada: " + id));
    }

    @Override
    public Pagina<Ocorrencia> listar(PaginaRequest paginaRequest) {
        return ocorrenciaRepository.findAtivas(paginaRequest);
    }

    @Override
    public Pagina<Ocorrencia> listarPorStatus(OcorrenciaEnum status, PaginaRequest paginaRequest) {
        return ocorrenciaRepository.findByStatus(status, paginaRequest);
    }

    @Override
    public Ocorrencia atualizar(AtualizarOcorrenciaCommand command) {
        return transacao.executar(() -> {
            Ocorrencia ocorrencia = buscar(command.id());

            Turma turma = command.schoolClassId() == null
                    ? ocorrencia.getTurma() : exigirTurma(command.schoolClassId());
            Aluno aluno = command.studentId() == null
                    ? ocorrencia.getStudent() : exigirAluno(command.studentId());
            CategoriaOcorrencia categoria = ehBranco(command.category())
                    ? ocorrencia.getCategory() : exigirCategoria(command.category());
            TipoOcorrencia tipo = ehBranco(command.type())
                    ? ocorrencia.getType() : exigirTipoDaCategoria(command.type(), categoria);

            // Trocar turma ou aluno reabre as mesmas regras do cadastro.
            if (mudouVinculo(ocorrencia, turma, aluno)) {
                validar(aluno, turma);
            }
            ocorrencia.atualizarDados(
                    turma, aluno, categoria, tipo, command.registerDate(), command.description());
            return ocorrenciaRepository.save(ocorrencia);
        });
    }

    @Override
    public Ocorrencia alterarStatus(AlterarStatusOcorrenciaCommand command) {
        OcorrenciaEnum novoStatus = OcorrenciaEnum.porSlugOuNome(command.status());
        if (novoStatus == null) {
            throw new RegraDeNegocioException("Status de ocorrencia desconhecido: " + command.status());
        }
        Ocorrencia salva = transacao.executar(() -> {
            Ocorrencia ocorrencia = buscar(command.id());
            ocorrencia.alterarStatus(novoStatus, command.updateDate());
            return ocorrenciaRepository.save(ocorrencia);
        });
        eventos.statusAlterado(salva);
        return salva;
    }

    @Override
    public void cancelar(Long id) {
        transacao.executar(() -> {
            Ocorrencia ocorrencia = buscar(id);
            ocorrencia.cancelar();
            return ocorrenciaRepository.save(ocorrencia);
        });
    }

    /**
     * Historico de um aluno, da mais recente para a mais antiga.
     *
     * <p>A versao anterior chamava {@code findAll()} de todas as ocorrencias do sistema e
     * imprimia no {@code System.out}; aqui a consulta e' filtrada pelo aluno e o resultado
     * volta para quem pediu.</p>
     */
    @Override
    public List<Ocorrencia> historicoDoAluno(Long alunoId) {
        exigirAluno(alunoId);
        return ocorrenciaRepository.findByAlunoId(alunoId).stream()
                .sorted(Comparator.comparing(Ocorrencia::getRegisterDate).reversed())
                .toList();
    }

    @Override
    public ResumoOcorrencias resumo() {
        Map<OcorrenciaEnum, Long> porStatus = new EnumMap<>(OcorrenciaEnum.class);
        for (OcorrenciaEnum status : OcorrenciaEnum.values()) {
            porStatus.put(status, ocorrenciaRepository.contarPorStatus(status));
        }
        return new ResumoOcorrencias(
                porStatus,
                ocorrenciaRepository.contarPorCategoria(),
                ocorrenciaRepository.contarPorTipo(),
                ocorrenciaRepository.contarPorTurma(),
                ocorrenciaRepository.contarPorAluno());
    }

    @Override
    public List<CategoriaOcorrencia> listarCategorias() {
        return categoriaRepository.findAll();
    }

    @Override
    public List<TipoOcorrencia> listarTiposPorCategoria(String nomeCategoria) {
        CategoriaOcorrencia categoria = exigirCategoria(nomeCategoria);
        return tipoRepository.findByCategoriaId(categoria.getId());
    }

    private void validar(Aluno aluno, Turma turma) {
        ContextoOcorrencia contexto = new ContextoOcorrencia(aluno, turma);
        validadores.forEach(validador -> validador.validar(contexto));
    }

    private boolean mudouVinculo(Ocorrencia ocorrencia, Turma turma, Aluno aluno) {
        return !turma.getId().equals(ocorrencia.getTurma().getId())
                || !aluno.getId().equals(ocorrencia.getStudent().getId());
    }

    private Turma exigirTurma(Long id) {
        if (id == null) {
            throw new RegraDeNegocioException("Turma da ocorrencia e' obrigatoria");
        }
        return turmaRepository.findById(id)
                .orElseThrow(() -> new TurmaNotFoundException("Turma nao encontrada: " + id));
    }

    private Aluno exigirAluno(Long id) {
        if (id == null) {
            throw new RegraDeNegocioException("Aluno da ocorrencia e' obrigatorio");
        }
        return alunoRepository.findById(id)
                .orElseThrow(() -> new AlunoNotFoundException("Aluno nao encontrado: " + id));
    }

    private CategoriaOcorrencia exigirCategoria(String nome) {
        if (ehBranco(nome)) {
            throw new RegraDeNegocioException("Categoria da ocorrencia e' obrigatoria");
        }
        return categoriaRepository.findByName(nome.trim())
                .orElseThrow(() -> new CategoriaNotFoundException("Categoria nao encontrada: " + nome));
    }

    private TipoOcorrencia exigirTipoDaCategoria(String nome, CategoriaOcorrencia categoria) {
        if (ehBranco(nome)) {
            throw new RegraDeNegocioException("Tipo da ocorrencia e' obrigatorio");
        }
        TipoOcorrencia tipo = tipoRepository.findByName(nome.trim())
                .orElseThrow(() -> new TipoOcorrenciaNotFoundException(
                        "Tipo de ocorrencia nao encontrado: " + nome));

        if (tipo.getCategoriaId() != null && !tipo.getCategoriaId().equals(categoria.getId())) {
            throw new RegraDeNegocioException(
                    "O tipo '" + tipo.getName() + "' nao pertence a categoria '"
                            + categoria.getName() + "'");
        }
        return tipo;
    }

    private static boolean ehBranco(String valor) {
        return valor == null || valor.isBlank();
   
}
