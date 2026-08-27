package br.com.project_sena.adapter.in.web.dto.response;

import java.util.List;
import java.util.function.Function;

import br.com.project_sena.application.core.domain.vo.Pagina;

/**
 * Envelope de paginacao.
 *
 * <p>Mantem os mesmos nomes de campo que o {@code Page} do Spring serializa
 * ({@code content}, {@code totalElements}, {@code number}, {@code first}, {@code last}),
 * que sao os que os scripts de listagem do front ja leem — mas sem devolver um tipo
 * interno do Spring Data no corpo da resposta.</p>
 */
public record PageResponse<T>(List<T> content,
                              int number,
                              int size,
                              long totalElements,
                              int totalPages,
                              boolean first,
                              boolean last) {

    public static <D, T> PageResponse<T> de(Pagina<D> pagina, Function<D, T> mapeador) {
        return new PageResponse<>(
                pagina.conteudo().stream().map(mapeador).toList(),
                pagina.pagina(),
                pagina.tamanho(),
                pagina.totalElementos(),
                pagina.totalPaginas(),
                pagina.primeira(),
                pagina.ultima());
    }
}
