package br.com.project_sena.application.port.out;

public interface AlunoRepository {
    Optional <Usuario> findById(Long id);
}
