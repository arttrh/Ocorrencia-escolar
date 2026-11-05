package br.com.project_sena.config;
import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import br.com.project_sena.application.core.domain.model.Usuario;
import br.com.project_sena.application.core.usecase.UsuarioService;
import br.com.project_sena.application.port.out.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
@Component
public class AdministradorInicial implements CommandLineRunner {
    private final UsuarioService service;
    private final UsuarioRepository repository;
    private final String email;
    private final String senha;
    public AdministradorInicial(UsuarioService service, UsuarioRepository repository,
        @Value("${ADMIN_EMAIL:}") String email, @Value("${ADMIN_PASSWORD:}") String senha) {
        this.service = service;
        this.repository = repository;
        this.email = email;
        this.senha = senha;
    }
    public void run(String... args) {
        if (!email.isBlank() && !senha.isBlank() && !repository.existsByEmail(email))
            service.cadastrar(new Usuario("Administrador", email, senha, PerfilEnum.ADMIN));
    }
}
