package br.com.project_sena.adapter.out.persistence;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import br.com.project_sena.application.core.domain.vo.Pagina;
import br.com.project_sena.application.core.domain.vo.PaginaRequest;

/**
 * Traducao entre a paginacao do dominio e a do Spring Data.
 *
 * <p>O campo de ordenacao vem da query string, ou seja, e' entrada do usuario. Passa-lo
 * direto para o {@code Sort} faz o Spring Data lancar
 * {@code PropertyReferenceException} (HTTP 500) para qualquer nome invalido e permite
 * sondar a estrutura interna das entidades. Por isso cada repositorio declara um mapa de
 * campos permitidos e tudo fora dele cai no padrao.</p>
 */
public final class PaginacaoSpring {

    private PaginacaoSpring() {
    }

    public static Pageable paraPageable(PaginaRequest request, Map<String, String> camposPermitidos) {
        String propriedade = camposPermitidos.getOrDefault(
                request.ordenarPor(), camposPermitidos.get("id"));
        if (propriedade == null) {
            propriedade = "id";
        }
        Sort.Direction direcao = request.direcao() == PaginaRequest.Direcao.DESC
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return PageRequest.of(request.pagina(), request.tamanho(), Sort.by(direcao, propriedade));
    }

    public static <E, D> Pagina<D> paraPagina(Page<E> page, java.util.function.Function<E, D> mapeador) {
        return new Pagina<>(
                page.getContent().stream().map(mapeador).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements());
    }
}
