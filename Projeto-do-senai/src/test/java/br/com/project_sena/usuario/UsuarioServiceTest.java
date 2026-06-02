package br.com.project_sena.usuario;
import br.com.project_sena.adapter.out.repository.entity.UsuarioEntity;
import br.com.project_sena.adapter.out.repository.persistence.UsuarioJpaRepository;
import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import br.com.project_sena.application.core.domain.enums.UsuarioEnum;
import br.com.project_sena.application.core.domain.model.Usuario;
import br.com.project_sena.application.core.usecase.UsuarioService;
import br.com.project_sena.application.port.out.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // liga o mockito ao JUnit
@Slf4j
public class UsuarioServiceTest {

    @Mock
    UsuarioRepository repo; //Mockito va cria uma versao falsa do repositorio

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UsuarioService service; // Injeta o mock no service automaticamente

    @Test
    @DisplayName("Expectativa: retornar o usuario")
    void retornarUsuarioQuandoEleExistir(){
        var usuario = new Usuario(
                1L, "arthur", "arthur@email.com", "arthur123", PerfilEnum.PROFESSOR, UsuarioEnum.ATIVO
        );
        when(repo.findById(1L)).thenReturn(Optional.of(usuario)); //Quando o metodo findById for chamado ele vai retornar o usuario
        //O Optional.of vai pegar o meu objeto e empacotar para dentro de um Optional presente

        //Chamar metodo Real
        var resultado = service.buscar(1L);

        assertEquals("arthur", resultado.getName()); // ele ira comparar o que o sistema quer com o que eu mandei
        verify(repo).findById(1L); // Confirmar que o repository foi chamado
    }

    @Test
    void lancarUmaExceptionQuandoEleNaoExistir(){
        when(repo.findById(99L)).thenReturn(Optional.empty()); // Verificar se o Usuario nao existe retornar Verdadeiro

        assertThrows(RuntimeException.class, () -> service.buscar(99L));
    }
}
