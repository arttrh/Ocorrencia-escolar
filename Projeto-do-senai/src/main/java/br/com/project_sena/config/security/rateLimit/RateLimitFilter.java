package br.com.project_sena.config.security.rateLimit;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Bucket bucket = RateLimitConfig.novoBucket();
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals("/login")){
            if (!bucket.tryConsume(1)){
                response.setStatus(429);
                response.getWriter()
                        .write("Muitas tentativas. Tente novamente mais tarde.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
