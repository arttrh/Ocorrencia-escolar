package br.com.project_sena.application.port.in;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface ModelDomainController<C, R, U, D, DET, ID> {
    ResponseEntity<DET> cadastrar(C dados);

    ResponseEntity<Page<R>> listar(Pageable pageable);

    ResponseEntity<DET> detalhar(ID id);

    ResponseEntity<DET> atualizar(U dados, ID id);

    ResponseEntity<D> excluir(ID id);
}
