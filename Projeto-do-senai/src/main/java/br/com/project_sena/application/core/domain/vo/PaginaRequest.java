package br.com.project_sena.application.core.domain.vo;

/**
 * Pedido de paginacao independente de framework.
 *
 * <p>Existe para que as portas de saida nao precisem falar {@code Pageable} do
 * Spring Data: a traducao para o mundo Spring acontece apenas no adaptador de
 * persistencia.</p>
 */
public record PaginaRequest(int pagina, int tamanho, String ordenarPor, Direcao direcao) {

    public static final int TAMANHO_MAXIMO = 100;

    public enum Direcao { ASC, DESC }

    public PaginaRequest {
        if (pagina < 0) {
            pagina = 0;
        }
        if (tamanho <= 0) {
            tamanho = 10;
        }
        if (tamanho > TAMANHO_MAXIMO) {
            tamanho = TAMANHO_MAXIMO;
        }
        if (ordenarPor == null || ordenarPor.isBlank()) {
            ordenarPor = "id";
        }
        if (direcao == null) {
            direcao = Direcao.ASC;
        }
    }

    public static PaginaRequest de(int pagina, int tamanho) {
        return new PaginaRequest(pagina, tamanho, "id", Direcao.ASC);
    }

    public static PaginaRequest padrao() {
        return de(0, 10);
    }
}
