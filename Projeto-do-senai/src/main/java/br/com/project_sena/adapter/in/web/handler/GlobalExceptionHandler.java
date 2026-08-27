package br.com.project_sena.adapter.in.web.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import br.com.project_sena.adapter.in.web.dto.response.ErroResponse;
import br.com.project_sena.application.core.domain.exception.AuthorizationException;
import br.com.project_sena.application.core.domain.exception.CredenciaisInvalidasException;
import br.com.project_sena.application.core.domain.exception.LimiteDeTentativasException;
import br.com.project_sena.application.core.domain.exception.RecursoNaoEncontradoException;
import br.com.project_sena.application.core.domain.exception.RegraDeNegocioException;
import br.com.project_sena.application.core.domain.exception.TokenInvalidoException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Traducao de excecoes de dominio para respostas HTTP.
 *
 * <p>Este e' o unico lugar do sistema que conhece codigos de status: as excecoes do
 * nucleo descrevem o problema de negocio, nao o protocolo.</p>
 *
 * <p>Diferencas em relacao ao {@code GlobalHandlerErrors} anterior:</p>
 * <ul>
 *   <li>o corpo de erro tem sempre o mesmo formato (antes ora era {@code String} pura, ora
 *       um {@code Stream}, que so' pode ser consumido uma vez);</li>
 *   <li>as excecoes sao tratadas pelas superclasses {@code RecursoNaoEncontrado} e
 *       {@code RegraDeNegocio}, entao uma excecao nova ja nasce com o status certo — antes,
 *       tipos sem handler (como {@code TurmaNotFoundException}) viravam 500;</li>
 *   <li>JSON malformado, enum invalido e upload grande demais tem tratamento proprio;</li>
 *   <li>erros inesperados nao expoem a mensagem interna ao cliente.</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> naoEncontrado(RecursoNaoEncontradoException ex,
                                                      HttpServletRequest request) {
        return resposta(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroResponse> regraDeNegocio(RegraDeNegocioException ex,
                                                       HttpServletRequest request) {
        return resposta(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler({CredenciaisInvalidasException.class, TokenInvalidoException.class})
    public ResponseEntity<ErroResponse> naoAutenticado(RuntimeException ex,
                                                       HttpServletRequest request) {
        return resposta(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErroResponse> semPermissao(AuthorizationException ex,
                                                     HttpServletRequest request) {
        return resposta(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    /** 429 com {@code Retry-After} para o cliente saber quando pode tentar de novo. */
    @ExceptionHandler(LimiteDeTentativasException.class)
    public ResponseEntity<ErroResponse> limiteDeTentativas(LimiteDeTentativasException ex,
                                                           HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header(HttpHeaders.RETRY_AFTER, String.valueOf(ex.getSegundosParaLiberar()))
                .body(ErroResponse.de(
                        HttpStatus.TOO_MANY_REQUESTS.value(),
                        HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(),
                        ex.getMessage(),
                        request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> validacao(MethodArgumentNotValidException ex,
                                                  HttpServletRequest request) {
        List<ErroResponse.CampoInvalido> campos = ex.getBindingResult().getFieldErrors().stream()
                .map(GlobalExceptionHandler::paraCampo)
                .toList();
        return ResponseEntity.badRequest().body(ErroResponse.comCampos(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Dados invalidos",
                request.getRequestURI(),
                campos));
    }

    /** JSON malformado, data fora do formato ou valor de enum inexistente. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> corpoIlegivel(HttpMessageNotReadableException ex,
                                                      HttpServletRequest request) {
        log.debug("Corpo da requisicao ilegivel: {}", ex.getMessage());
        return resposta(HttpStatus.BAD_REQUEST,
                "Corpo da requisicao invalido. Confira os tipos e os formatos de data.", request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> tipoInvalido(MethodArgumentTypeMismatchException ex,
                                                     HttpServletRequest request) {
        return resposta(HttpStatus.BAD_REQUEST,
                "Valor invalido para o parametro '" + ex.getName() + "'", request);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErroResponse> uploadGrande(MaxUploadSizeExceededException ex,
                                                     HttpServletRequest request) {
        return resposta(HttpStatus.PAYLOAD_TOO_LARGE, "Arquivo enviado e' grande demais", request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> conflito(DataIntegrityViolationException ex,
                                                 HttpServletRequest request) {
        log.warn("Violacao de integridade em {}: {}", request.getRequestURI(), ex.getMessage());
        return resposta(HttpStatus.CONFLICT,
                "A operacao conflita com dados ja existentes", request);
    }

    /**
     * Rede de seguranca. A mensagem original vai para o log, nunca para a resposta: ela
     * pode conter nomes de tabela, SQL ou caminhos do servidor.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> inesperado(Exception ex, HttpServletRequest request) {
        log.error("Erro inesperado em {} {}", request.getMethod(), request.getRequestURI(), ex);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno. Tente novamente ou contate o suporte.", request);
    }

    private static ErroResponse.CampoInvalido paraCampo(FieldError erro) {
        return new ErroResponse.CampoInvalido(erro.getField(), erro.getDefaultMessage());
    }

    private static ResponseEntity<ErroResponse> resposta(HttpStatus status, String mensagem,
                                                         HttpServletRequest request) {
        return ResponseEntity.status(status).body(ErroResponse.de(
                status.value(), status.getReasonPhrase(), mensagem, request.getRequestURI()));
    }
}
