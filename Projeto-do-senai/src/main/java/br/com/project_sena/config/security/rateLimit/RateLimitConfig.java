package br.com.project_sena.config.security.rateLimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

import java.time.Duration;

public class RateLimitConfig {

    public static Bucket novoBucket(){ //Chamando o objeto Bucket(Balde)
        Bandwidth limit = Bandwidth.classic( //Definindo regras no Bucket
                5, //sao 5 requisicoes que o usuario pode fazer
                Refill.greedy(5, Duration.ofMinutes(1)) //A forma como vao repor elas greendy e para todas voltarem de uma vez
        );

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
