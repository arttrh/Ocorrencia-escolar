package br.com.project_sena.config.security.rateLimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket getBucket(String ip){
        return buckets.computeIfAbsent(ip, k -> {
            Bandwidth limit = Bandwidth.classic(
                    5,
                    Refill.greedy(5, Duration.ofMinutes(1))
            );
            return Bucket.builder()
                    .addLimit(limit)
                    .build();
        });
    }
}
