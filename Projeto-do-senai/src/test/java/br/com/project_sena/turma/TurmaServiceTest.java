package br.com.project_sena.turma;

import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import br.com.project_sena.application.core.domain.model.Aluno;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.core.usecase.TurmaService;
import br.com.project_sena.application.port.out.AlunoRepository;
import br.com.project_sena.application.port.out.TurmaRepository;
import br.com.project_sena.exception.type.AlunoNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TurmaServiceTest {

    @Mock
    private TurmaRepository repository;

    @Mock
    private AlunoRepository alunoRepository;

    @InjectMocks
    private TurmaService service;

    @Test
    @DisplayName("Deve cadastrar uma turma com sucesso quando o aluno existe")
    void deveCadastrarTurmaComSucesso() {
        Aluno aluno = new Aluno(1L, "foto.jpg", "João Silva", LocalDate.of(2010, 5, 20), AlunoEnum.ATIVO);
        Turma turmaInput = new Turma(null, "3A", "Matutino", 2026, TurmaEnum.ATIVO);
        Turma turmaSalva = new Turma(1L, "3A", "Matutino", 2026, TurmaEnum.ATIVO);

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(repository.save(any(Turma.class))).thenReturn(turmaSalva);

        Turma resultado = service.cadastrar(turmaInput, 1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("3A", resultado.getClassName());
        verify(alunoRepository, times(1)).findById(1L);
        verify(repository, times(1)).save(turmaInput);
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar turma para aluno inexistente")
    void deveLancarExcecaoAoCadastrarTurmaParaAlunoInexistente() {
        Turma turmaInput = new Turma(null, "3A", "Matutino", 2026, TurmaEnum.ATIVO);

        when(alunoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(AlunoNotFoundException.class, () -> service.cadastrar(turmaInput, 99L));
        verify(alunoRepository, times(1)).findById(99L);
        verify(repository, never()).save(any(Turma.class));
    }
}
