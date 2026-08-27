package br.com.project_sena.adapter.in.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.project_sena.adapter.in.web.dto.request.LoginRequest;
import br.com.project_sena.adapter.in.web.dto.response.LoginResponse;
import br.com.project_sena.application.port.in.AutenticarUsuarioUseCase;
import br.com.project_sena.application.port.in.command.CredenciaisCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Autenticacao.
 *
 * <p>O controller so' traduz HTTP: quem aplica o rate limit, compara a senha e emite o
 * token e' o use case. Na versao anterior o controller pedia um bucket ao
 * {@code RateLimitService} e descartava o resultado, de modo que o limite por IP nunca
 * era aplicado de fato.</p>
 */
@RestController
@RequestMapping("/login")
@Tag(name = "Autenticacao", description = "Emissao de token de acesso")
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final AutenticarUsuarioUseCase autenticarUsuario;
    private final boolean confiarEmProxy;

    public LoginController(AutenticarUsuarioUseCase autenticarUsuario,
                           @Value("${app.security.trusted-proxy:false}") boolean confiarEmProxy) {
        this.autenticarUsuario = autenticarUsuario;
        this.confiarEmProxy = confiarEmProxy;
    }

    @PostMapping
    @Operation(summary = "Autenticar", description = "Valida as credenciais e devolve o token JWT")
    public ResponseEntity<LoginResponse> logar(@RequestBody @Valid LoginRequest request,
                                               HttpServletRequest httpRequest) {
        String origem = origemDaRequisicao(httpRequest);
        log.debug("Tentativa de login para {} vinda de {}", request.login(), origem);

        AutenticarUsuarioUseCase.Resultado resultado = autenticarUsuario.autenticar(
                new CredenciaisCommand(request.login(), request.password(), origem));

        return ResponseEntity.ok(new LoginResponse(
                resultado.token(), resultado.usuarioId(), resultado.login(), resultado.perfil()));
    }

    /**
     * Identifica a origem para o rate limit.
     *
     * <p>{@code X-Forwarded-For} so' e' considerado quando a aplicacao esta declaradamente
     * atras de um proxy confiavel: aceitar o cabecalho sempre permitiria a qualquer cliente
     * forjar um IP diferente a cada tentativa e escapar do limite.</p>
     */
    private String origemDaRequisicao(HttpServletRequest request) {
        if (confiarEmProxy) {
            String encaminhado = request.getHeader("X-Forwarded-For");
            if (encaminhado != null && !encaminhado.isBlank()) {
                return encaminhado.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }
}
