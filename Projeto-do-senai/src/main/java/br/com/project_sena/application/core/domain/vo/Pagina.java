package br.com.project_sena.application.core.domain.vo;

import java.util.List;
import java.util.function.Function;

/**
 * Resultado paginado independente de framework, espelho do {@code Page} do Spring
 * Data mas sem depender dele.
 */
public record Pagina<T>(List<T> conteudo, int pagina, int tamanho, long totalElementos) {

    public Pagina {
        conteudo = conteudo == null ? List.of() : List.copyOf(conteudo);
    }

    public static <T> Pagina<T> vazia(PaginaRequest request) {
        return new Pagina<>(List.of(), request.pagina(), request.tamanho(), 0L);
    }

    public int totalPaginas() {
        if (tamanho <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalElementos / (double) tamanho);
    }

    public boolean primeira() {
        return pagina == 0;
    }

    public boolean ultima() {
        return pagina >= totalPaginas() - 1;
    }

    public <R> Pagina<R> mapear(Function<T, R> mapeador) {
        return new Pagina<>(conteudo.stream().map(mapeador).toList(), pagina, tamanho, totalElementos);
    }
}
