package br.com.project_sena.adapter.in.web.mapper;

import br.com.project_sena.application.core.domain.vo.PaginaRequest;

/**
 * Monta o {@link PaginaRequest} do dominio a partir dos parametros de query.
 *
 * <p>Existe para que os controllers nao recebam {@code Pageable} do Spring Data: o tipo
 * do framework fica confinado ao adaptador de persistencia.</p>
 */
public final class PaginacaoWeb {

    private PaginacaoWeb() {
    }

    public static PaginaRequest de(Integer page, Integer size, String sort, String direction) {
        PaginaRequest.Direcao direcao = "desc".equalsIgnoreCase(direction)
                ? PaginaRequest.Direcao.DESC
                : PaginaRequest.Direcao.ASC;
        return new PaginaRequest(
                page == null ? 0 : page,
                size == null ? 10 : size,
                sort,
                direcao);
    }
}
