package br.com.project_sena.adapter.out.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.project_sena.application.port.out.CriptografiaPort;

/** Implementa {@link CriptografiaPort} sobre o {@code PasswordEncoder} do Spring. */
@Component
public class BCryptCriptografiaAdapter implements CriptografiaPort {

    private final PasswordEncoder passwordEncoder;

    public BCryptCriptografiaAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String codificar(String senhaEmTextoPuro) {
        return passwordEncoder.encode(senhaEmTextoPuro);
    }

    @Override
    public boolean confere(String senhaEmTextoPuro, String senhaCodificada) {
        if (senhaEmTextoPuro == null || senhaCodificada == null) {
            return false;
        }
        return passwordEncoder.matches(senhaEmTextoPuro, senhaCodificada);
    }
}
