package br.com.project_sena.aluno;

import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.usecase.AlunoService;
import br.com.project_sena.application.port.out.AlunoRepository;
import br.com.project_sena.exception.type.AlunoExistingException;
import br.com.project_sena.exception.type.AlunoNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlunoServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    @InjectMocks
    private AlunoService alunoService;

    @Test
    @DisplayName("Deve cadastrar um aluno com sucesso")
    void deveCadastrarAlunoComSucesso() {
        Aluno aluno = new Aluno(null, "foto.jpg", "João Silva", LocalDate.of(2010, 5, 20), AlunoEnum.ATIVO);
        Aluno alunoSalvo = new Aluno(1L, "foto.jpg", "João Silva", LocalDate.of(2010, 5, 20), AlunoEnum.ATIVO);

        when(alunoRepository.save(any(Aluno.class))).thenReturn(alunoSalvo);

        Aluno resultado = alunoService.cadastrar(aluno);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("João Silva", resultado.getName());
        verify(alunoRepository, times(1)).save(aluno);
    }

    @Test
    @DisplayName("Deve buscar um aluno por ID com sucesso")
    void deveBuscarAlunoPorIdComSucesso() {
        Aluno aluno = new Aluno(1L, "foto.jpg", "João Silva", LocalDate.of(2010, 5, 20), AlunoEnum.ATIVO);

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));

        Aluno resultado = alunoService.buscar(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(alunoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar aluno inexistente")
    void deveLancarExcecaoAoBuscarAlunoInexistente() {
        when(alunoRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> alunoService.buscar(99L));
        assertEquals("Aluno nao encontrado", exception.getMessage());
        verify(alunoRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve listar alunos ativos")
    void deveListarAlunosAtivos() {
        Pageable pageable = PageRequest.of(0, 10);
        Aluno aluno = new Aluno(1L, "foto.jpg", "João Silva", LocalDate.of(2010, 5, 20), AlunoEnum.ATIVO);
        Page<Aluno> page = new PageImpl<>(List.of(aluno));

        when(alunoRepository.findByAlunoEnum(pageable, AlunoEnum.ATIVO)).thenReturn(page);

        Page<Aluno> resultado = alunoService.listarAtivos(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(AlunoEnum.ATIVO, resultado.getContent().get(0).getAlunoEnum());
        verify(alunoRepository, times(1)).findByAlunoEnum(pageable, AlunoEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve listar alunos inativos")
    void deveListarAlunosInativos() {
        Pageable pageable = PageRequest.of(0, 10);
        Aluno aluno = new Aluno(1L, "foto.jpg", "João Silva", LocalDate.of(2010, 5, 20), AlunoEnum.INVATIVO);
        Page<Aluno> page = new PageImpl<>(List.of(aluno));

        when(alunoRepository.findByAlunoEnum(pageable, AlunoEnum.INVATIVO)).thenReturn(page);

        Page<Aluno> resultado = alunoService.listarInativos(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(AlunoEnum.INVATIVO, resultado.getContent().get(0).getAlunoEnum());
        verify(alunoRepository, times(1)).findByAlunoEnum(pageable, AlunoEnum.INVATIVO);
    }

    @Test
    @DisplayName("Deve atualizar dados do aluno com sucesso")
    void deveAtualizarAlunoComSucesso() {
        Aluno alunoExistente = new Aluno(1L, "foto.jpg", "João Silva", LocalDate.of(2010, 5, 20), AlunoEnum.ATIVO);
        Aluno dadosAtualizacao = new Aluno(1L, "nova_foto.jpg", "João S. Silva", LocalDate.of(2010, 5, 21), AlunoEnum.ATIVO);

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(alunoExistente));
        when(alunoRepository.save(any(Aluno.class))).thenReturn(alunoExistente);

        Aluno resultado = alunoService.atualizar(1L, dadosAtualizacao);

        assertNotNull(resultado);
        assertEquals("nova_foto.jpg", resultado.getPhoto());
        assertEquals("João S. Silva", resultado.getName());
        assertEquals(LocalDate.of(2010, 5, 21), resultado.getDateBirth());
        verify(alunoRepository, times(1)).findById(1L);
        verify(alunoRepository, times(1)).save(alunoExistente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar aluno inexistente")
    void deveLancarExcecaoAoAtualizarAlunoInexistente() {
        Aluno dadosAtualizacao = new Aluno(99L, "nova_foto.jpg", "João S. Silva", LocalDate.of(2010, 5, 21), AlunoEnum.ATIVO);

        when(alunoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(AlunoNotFoundException.class, () -> alunoService.atualizar(99L, dadosAtualizacao));
        verify(alunoRepository, times(1)).findById(99L);
        verify(alunoRepository, never()).save(any(Aluno.class));
    }

    @Test
    @DisplayName("Deve excluir (inativar) aluno com sucesso")
    void deveExcluirAlunoComSucesso() {
        Aluno aluno = new Aluno(1L, "foto.jpg", "João Silva", LocalDate.of(2010, 5, 20), AlunoEnum.ATIVO);

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(alunoRepository.save(any(Aluno.class))).thenReturn(aluno);

        alunoService.excluir(1L);

        assertEquals(AlunoEnum.INVATIVO, aluno.getAlunoEnum());
        verify(alunoRepository, times(1)).findById(1L);
        verify(alunoRepository, times(1)).save(aluno);
    }

    @Test
    @DisplayName("Deve reativar aluno com sucesso")
    void deveReativarAlunoComSucesso() {
        Aluno aluno = new Aluno(1L, "foto.jpg", "João Silva", LocalDate.of(2010, 5, 20), AlunoEnum.INVATIVO);

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(alunoRepository.save(any(Aluno.class))).thenReturn(aluno);

        Aluno resultado = alunoService.reativar(1L);

        assertNotNull(resultado);
        assertEquals(AlunoEnum.ATIVO, resultado.getAlunoEnum());
        verify(alunoRepository, times(1)).findById(1L);
        verify(alunoRepository, times(1)).save(aluno);
    }

    @Test
    @DisplayName("Deve lançar exceção ao reativar aluno que já está ativo")
    void deveLancarExcecaoAoReativarAlunoJaAtivo() {
        Aluno aluno = new Aluno(1L, "foto.jpg", "João Silva", LocalDate.of(2010, 5, 20), AlunoEnum.ATIVO);

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));

        assertThrows(AlunoExistingException.class, () -> alunoService.reativar(1L));
        verify(alunoRepository, times(1)).findById(1L);
        verify(alunoRepository, never()).save(any(Aluno.class));
    }
}
