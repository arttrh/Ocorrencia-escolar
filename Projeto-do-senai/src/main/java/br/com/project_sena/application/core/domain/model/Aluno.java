package br.com.project_sena.application.core.domain.model;

import java.time.LocalDate;
import java.util.Objects;

import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.exception.AlunoExistingException;
import br.com.project_sena.application.core.domain.exception.RegraDeNegocioException;

public class Aluno {

    private Long id;
    private String name;
    private LocalDate birthDate;
    private String imageUrl;
    private AlunoEnum status;

    public Aluno(Long id, String name, LocalDate birthDate, String imageUrl, AlunoEnum status) {
        this.id = id;
        this.name = exigirTexto(name, "Nome do aluno e' obrigatorio");
        this.birthDate = exigirNascimentoValido(birthDate);
        this.imageUrl = imageUrl;
        this.status = status == null ? AlunoEnum.ATIVO : status;
    }

    public static Aluno novo(String name, LocalDate birthDate) {
        return new Aluno(null, name, birthDate, null, AlunoEnum.ATIVO);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public AlunoEnum getStatus() {
        return status;
    }

    public boolean isAtivo() {
        return status.isAtivo();
    }

    public void atualizarDados(String name, LocalDate birthDate) {
        if (name != null && !name.isBlank()) {
            this.name = name.trim();
        }
        if (birthDate != null) {
            this.birthDate = exigirNascimentoValido(birthDate);
        }
    }

    public void alterarFoto(String imageUrl) {
        this.imageUrl = exigirTexto(imageUrl, "Imagem do aluno e' obrigatoria");
    }

    public void inativar() {
        this.status = AlunoEnum.INATIVO;
    }

    public void reativar() {
        if (isAtivo()) {
            throw new AlunoExistingException("Aluno ja esta ativo");
        }
        this.status = AlunoEnum.ATIVO;
    }

    private static LocalDate exigirNascimentoValido(LocalDate birthDate) {
        if (birthDate == null) {
            throw new RegraDeNegocioException("Data de nascimento e' obrigatoria");
        }
        if (birthDate.isAfter(LocalDate.now())) {
            throw new RegraDeNegocioException("Data de nascimento nao pode estar no futuro");
        }
        return birthDate;
    }

    private static String exigirTexto(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new RegraDeNegocioException(mensagem);
        }
        return valor.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Aluno outro)) {
            return false;
        }
        return id != null && Objects.equals(id, outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
