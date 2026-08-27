package br.com.project_sena.application.port.out;

/**
 * Controle de taxa de tentativas.
 *
 * <p>Modelado como porta para que a regra "quantas tentativas de login sao aceitas"
 * pertenca a aplicacao, e a implementacao (Bucket4j em memoria hoje, Redis amanha)
 * fique no adaptador.</p>
 */
public interface RateLimiterPort {

    /** @return o veredito da tentativa; nao lanca excecao para permitir uso em filtro */
    Veredito consumir(String chave);

    record Veredito(boolean permitido, long tentativasRestantes, long segundosParaLiberar) {

        public static Veredito permitido(long restantes) {
            return new Veredito(true, restantes, 0L);
        }

        public static Veredito bloqueado(long segundosParaLiberar) {
            return new Veredito(false, 0L, segundosParaLiberar);
        }
    }
}
