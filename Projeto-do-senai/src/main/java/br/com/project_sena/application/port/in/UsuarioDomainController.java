package br.com.project_sena.application.port.in;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

public interface UsuarioDomainController<C, R, RI, U, D, DR, DET, ID>{
    ResponseEntity<DET> cadastrar(C dados, UriComponentsBuilder uriBuilder);

    ResponseEntity<Page<R>> listarAtivos(Pageable pageable);

    ResponseEntity<Page<RI>> listarInativos(Pageable pageable);

    ResponseEntity<DET> atualizar(U dados, ID id);

    ResponseEntity<D> excluir(ID id);

    ResponseEntity<DR> reativar(ID id);

    ResponseEntity<DET> detalhar(ID id);
}