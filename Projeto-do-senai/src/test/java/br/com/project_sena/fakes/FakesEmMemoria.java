package br.com.project_sena.fakes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.enums.OcorrenciaEnum;
import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import br.com.project_sena.application.core.domain.enums.UsuarioEnum;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.model.CategoriaOcorrencia;
import br.com.project_sena.application.core.domain.model.Ocorrencia;
import br.com.project_sena.application.core.domain.model.TipoOcorrencia;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.core.domain.model.Usuario;
import br.com.project_sena.application.core.domain.vo.ContagemPorChave;
import br.com.project_sena.application.core.domain.vo.Pagina;
import br.com.project_sena.application.core.domain.vo.PaginaRequest;
import br.com.project_sena.application.port.out.AlunoRepository;
import br.com.project_sena.application.port.out.CategoriaOcorrenciaRepository;
import br.com.project_sena.application.port.out.CriptografiaPort;
import br.com.project_sena.application.port.out.EventoOcorrenciaPort;
import br.com.project_sena.application.port.out.OcorrenciaRepository;
import br.com.project_sena.application.port.out.RateLimiterPort;
import br.com.project_sena.application.port.out.TipoOcorrenciaRepository;
import br.com.project_sena.application.port.out.TokenPort;
import br.com.project_sena.application.port.out.TransacaoPort;
import br.com.project_sena.application.port.out.TurmaRepository;
import br.com.project_sena.application.port.out.UsuarioRepository;
import br.com.project_sena.application.port.out.VinculoRepository;

/**
 * Implementacoes em memoria das portas de saida.
 *
 * <p>Sao dublês de verdade, nao mocks: exercitam o use case contra um comportamento real
 * e consistente. Escrever isto e' trivial justamente porque as portas nao falam
 * {@code Page}, {@code Pageable} nem tipos de JPA — o motivo pratico de mante-las livres
 * de framework.</p>
 */
public final class FakesEmMemoria {

    private FakesEmMemoria() {
    }

    /** Executa a operacao direto, sem transacao — suficiente para teste de unidade. */
    public static TransacaoPort transacaoDireta() {
        return new TransacaoPort() {
            @Override
            public <T> T executar(java.util.function.Supplier<T> operacao) {
                return operacao.get();
            }
        };
    }

    /** "Criptografia" reversivel e previsivel, para o teste nao gastar tempo em BCrypt. */
    public static CriptografiaPort criptografiaFalsa() {
        return new CriptografiaPort() {
            @Override
            public String codificar(String senha) {
                return "cod:" + senha;
            }

            @Override
            public boolean confere(String senha, String codificada) {
                return codificada != null && codificada.equals("cod:" + senha);
            }
        };
    }

    public static TokenPort tokenFalso() {
        return new TokenPort() {
            @Override
            public String gerarToken(Usuario usuario) {
                return "token-de-" + usuario.getLogin();
            }

            @Override
            public Optional<String> extrairLogin(String token) {
                return token != null && token.startsWith("token-de-")
                        ? Optional.of(token.substring("token-de-".length()))
                        : Optional.empty();
            }
        };
    }

    /** Rate limiter que sempre permite. Use {@link RateLimiterContado} para testar o limite. */
    public static RateLimiterPort rateLimiterPermissivo() {
        return chave -> RateLimiterPort.Veredito.permitido(Long.MAX_VALUE);
    }

    /** Permite N tentativas por chave e bloqueia dali em diante. */
    public static final class RateLimiterContado implements RateLimiterPort {

        private final int limite;
        private final Map<String, Integer> usos = new ConcurrentHashMap<>();

        public RateLimiterContado(int limite) {
            this.limite = limite;
        }

        @Override
        public Veredito consumir(String chave) {
            int usadas = usos.merge(chave, 1, Integer::sum);
            return usadas <= limite
                    ? Veredito.permitido(limite - usadas)
                    : Veredito.bloqueado(60);
        }
    }

    /** Guarda os eventos publicados para que o teste possa afirmar sobre eles. */
    public static final class EventosCapturados implements EventoOcorrenciaPort {

        public final List<Ocorrencia> registradas = new ArrayList<>();
        public final List<Ocorrencia> statusAlterados = new ArrayList<>();

        @Override
        public void ocorrenciaRegistrada(Ocorrencia ocorrencia) {
            registradas.add(ocorrencia);
        }

        @Override
        public void statusAlterado(Ocorrencia ocorrencia) {
            statusAlterados.add(ocorrencia);
        }
    }

    // ------------------------------------------------------------------ //
    // Repositorios
    // ------------------------------------------------------------------ //

    public static final class UsuarioRepositorioFake implements UsuarioRepository {

        private final Map<Long, Usuario> dados = new ConcurrentHashMap<>();
        private final AtomicLong sequencia = new AtomicLong();

        @Override
        public Usuario save(Usuario usuario) {
            Long id = usuario.getId() == null ? sequencia.incrementAndGet() : usuario.getId();
            Usuario salvo = new Usuario(id, usuario.getName(), usuario.getLogin(),
                    usuario.getPassword(), usuario.getPerfil(), usuario.getStatus());
            dados.put(id, salvo);
            return salvo;
        }

        @Override
        public Optional<Usuario> findById(Long id) {
            return Optional.ofNullable(dados.get(id));
        }

        @Override
        public Optional<Usuario> findByLogin(String login) {
            return dados.values().stream()
                    .filter(u -> u.getLogin().equalsIgnoreCase(login))
                    .findFirst();
        }

        @Override
        public boolean existsByLogin(String login) {
            return findByLogin(login).isPresent();
        }

        @Override
        public boolean existsByLoginAndIdNot(String login, Long id) {
            return dados.values().stream()
                    .anyMatch(u -> u.getLogin().equalsIgnoreCase(login) && !u.getId().equals(id));
        }

        @Override
        public Pagina<Usuario> findByStatus(UsuarioEnum status, PaginaRequest request) {
            return paginar(dados.values(), u -> u.getStatus() == status,
                    Comparator.comparing(Usuario::getId), request);
        }
    }

    public static final class AlunoRepositorioFake implements AlunoRepository {

        private final Map<Long, Aluno> dados = new ConcurrentHashMap<>();
        private final AtomicLong sequencia = new AtomicLong();

        @Override
        public Aluno save(Aluno aluno) {
            Long id = aluno.getId() == null ? sequencia.incrementAndGet() : aluno.getId();
            Aluno salvo = new Aluno(id, aluno.getName(), aluno.getBirthDate(),
                    aluno.getImageUrl(), aluno.getStatus());
            dados.put(id, salvo);
            return salvo;
        }

        @Override
        public Optional<Aluno> findById(Long id) {
            return Optional.ofNullable(dados.get(id));
        }

        @Override
        public Pagina<Aluno> findByStatus(AlunoEnum status, PaginaRequest request) {
            return paginar(dados.values(), a -> a.getStatus() == status,
                    Comparator.comparing(Aluno::getId), request);
        }
    }

    public static final class TurmaRepositorioFake implements TurmaRepository {

        private final Map<Long, Turma> dados = new ConcurrentHashMap<>();
        private final AtomicLong sequencia = new AtomicLong();

        @Override
        public Turma save(Turma turma) {
            Long id = turma.getId() == null ? sequencia.incrementAndGet() : turma.getId();
            Turma salva = new Turma(id, turma.getName(), turma.getShift(), turma.getYear(),
                    turma.getSemester(), turma.getStatus());
            dados.put(id, salva);
            return salva;
        }

        @Override
        public Optional<Turma> findById(Long id) {
            return Optional.ofNullable(dados.get(id));
        }

        @Override
        public Pagina<Turma> findByStatus(TurmaEnum status, PaginaRequest request) {
            return paginar(dados.values(), t -> t.getStatus() == status,
                    Comparator.comparing(Turma::getId), request);
        }
    }

    public static final class VinculoRepositorioFake implements VinculoRepository {

        private final Map<Long, Long> turmaPorAluno = new ConcurrentHashMap<>();
        private final Map<Long, Aluno> alunos = new ConcurrentHashMap<>();

        public void registrarAluno(Aluno aluno) {
            alunos.put(aluno.getId(), aluno);
        }

        @Override
        public void vincular(Long alunoId, Long turmaId) {
            turmaPorAluno.put(alunoId, turmaId);
        }

        @Override
        public long contarAlunosDaTurma(Long turmaId) {
            return turmaPorAluno.values().stream().filter(turmaId::equals).count();
        }

        @Override
        public List<Aluno> listarAlunosDaTurma(Long turmaId) {
            return turmaPorAluno.entrySet().stream()
                    .filter(e -> e.getValue().equals(turmaId))
                    .map(e -> alunos.get(e.getKey()))
                    .filter(java.util.Objects::nonNull)
                    .sorted(Comparator.comparing(Aluno::getName))
                    .toList();
        }

        @Override
        public boolean alunoPertenceATurma(Long alunoId, Long turmaId) {
            return turmaId.equals(turmaPorAluno.get(alunoId));
        }

        @Override
        public Optional<Long> turmaDoAluno(Long alunoId) {
            return Optional.ofNullable(turmaPorAluno.get(alunoId));
        }
    }

    public static final class CategoriaRepositorioFake implements CategoriaOcorrenciaRepository {

        private final Map<Long, CategoriaOcorrencia> dados = new ConcurrentHashMap<>();
        private final AtomicLong sequencia = new AtomicLong();

        @Override
        public CategoriaOcorrencia save(CategoriaOcorrencia categoria) {
            Long id = categoria.getId() == null ? sequencia.incrementAndGet() : categoria.getId();
            CategoriaOcorrencia salva = new CategoriaOcorrencia(id, categoria.getName());
            dados.put(id, salva);
            return salva;
        }

        @Override
        public Optional<CategoriaOcorrencia> findById(Long id) {
            return Optional.ofNullable(dados.get(id));
        }

        @Override
        public Optional<CategoriaOcorrencia> findByName(String name) {
            return dados.values().stream()
                    .filter(c -> c.getName().equalsIgnoreCase(name))
                    .findFirst();
        }

        @Override
        public List<CategoriaOcorrencia> findAll() {
            return dados.values().stream()
                    .sorted(Comparator.comparing(CategoriaOcorrencia::getName))
                    .toList();
        }
    }

    public static final class TipoRepositorioFake implements TipoOcorrenciaRepository {

        private final Map<Long, TipoOcorrencia> dados = new ConcurrentHashMap<>();
        private final AtomicLong sequencia = new AtomicLong();

        @Override
        public TipoOcorrencia save(TipoOcorrencia tipo) {
            Long id = tipo.getId() == null ? sequencia.incrementAndGet() : tipo.getId();
            TipoOcorrencia salvo = new TipoOcorrencia(id, tipo.getName(), tipo.getCategoriaId());
            dados.put(id, salvo);
            return salvo;
        }

        @Override
        public Optional<TipoOcorrencia> findById(Long id) {
            return Optional.ofNullable(dados.get(id));
        }

        @Override
        public Optional<TipoOcorrencia> findByName(String name) {
            return dados.values().stream()
                    .filter(t -> t.getName().equalsIgnoreCase(name))
                    .findFirst();
        }

        @Override
        public List<TipoOcorrencia> findByCategoriaId(Long categoriaId) {
            return dados.values().stream()
                    .filter(t -> categoriaId.equals(t.getCategoriaId()))
                    .sorted(Comparator.comparing(TipoOcorrencia::getName))
                    .toList();
        }

        @Override
        public List<TipoOcorrencia> findAll() {
            return List.copyOf(dados.values());
        }
    }

    public static final class OcorrenciaRepositorioFake implements OcorrenciaRepository {

        private final Map<Long, Ocorrencia> dados = new ConcurrentHashMap<>();
        private final AtomicLong sequencia = new AtomicLong();

        @Override
        public Ocorrencia save(Ocorrencia ocorrencia) {
            Long id = ocorrencia.getId() == null ? sequencia.incrementAndGet() : ocorrencia.getId();
            Ocorrencia salva = new Ocorrencia(id, ocorrencia.getTurma(), ocorrencia.getStudent(),
                    ocorrencia.getCategory(), ocorrencia.getType(), ocorrencia.getRegisterDate(),
                    ocorrencia.getDescription(), ocorrencia.getStatus(), ocorrencia.getUpdateDate(),
                    ocorrencia.isDeleted(), ocorrencia.getCreatedAt());
            dados.put(id, salva);
            return salva;
        }

        @Override
        public Optional<Ocorrencia> findById(Long id) {
            return Optional.ofNullable(dados.get(id));
        }

        @Override
        public Pagina<Ocorrencia> findAtivas(PaginaRequest request) {
            return paginar(dados.values(), o -> !o.isDeleted(),
                    Comparator.comparing(Ocorrencia::getId), request);
        }

        @Override
        public Pagina<Ocorrencia> findByStatus(OcorrenciaEnum status, PaginaRequest request) {
            return paginar(dados.values(), o -> !o.isDeleted() && o.getStatus() == status,
                    Comparator.comparing(Ocorrencia::getId), request);
        }

        @Override
        public List<Ocorrencia> findByAlunoId(Long alunoId) {
            return dados.values().stream()
                    .filter(o -> !o.isDeleted() && o.getStudent().getId().equals(alunoId))
                    .toList();
        }

        @Override
        public long contarPorStatus(OcorrenciaEnum status) {
            return dados.values().stream()
                    .filter(o -> !o.isDeleted() && o.getStatus() == status)
                    .count();
        }

        @Override
        public List<ContagemPorChave> contarPorCategoria() {
            return agrupar(o -> o.getCategory().getName());
        }

        @Override
        public List<ContagemPorChave> contarPorTipo() {
            return agrupar(o -> o.getType().getName());
        }

        @Override
        public List<ContagemPorChave> contarPorTurma() {
            return agrupar(o -> o.getTurma().getName());
        }

        @Override
        public List<ContagemPorChave> contarPorAluno() {
            return agrupar(o -> o.getStudent().getName());
        }

        private List<ContagemPorChave> agrupar(Function<Ocorrencia, String> chave) {
            return dados.values().stream()
                    .filter(o -> !o.isDeleted())
                    .collect(Collectors.groupingBy(chave, Collectors.counting()))
                    .entrySet().stream()
                    .map(e -> new ContagemPorChave(e.getKey(), e.getValue()))
                    .sorted(Comparator.comparingLong(ContagemPorChave::value).reversed())
                    .toList();
        }
    }

    private static <T> Pagina<T> paginar(java.util.Collection<T> origem,
                                         Predicate<T> filtro,
                                         Comparator<T> ordem,
                                         PaginaRequest request) {
        List<T> filtrados = origem.stream().filter(filtro).sorted(ordem).toList();
        int inicio = Math.min(request.pagina() * request.tamanho(), filtrados.size());
        int fim = Math.min(inicio + request.tamanho(), filtrados.size());
        return new Pagina<>(filtrados.subList(inicio, fim),
                request.pagina(), request.tamanho(), filtrados.size());
    }
}
