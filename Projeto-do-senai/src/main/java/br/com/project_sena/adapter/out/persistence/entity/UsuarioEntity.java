package br.com.project_sena.adapter.out.persistence.entity;

import java.util.Objects;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import br.com.project_sena.application.core.domain.enums.UsuarioEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Tabela {@code usuario}.
 *
 * <p>Nao implementa mais {@code UserDetails}: o contrato do Spring Security foi movido
 * para {@code adapter.out.security.UsuarioPrincipal}, deixando esta classe como um
 * registro de persistencia puro.</p>
 */
@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "login", nullable = false, unique = true, length = 255)
    private String login;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "profile", nullable = false, length = 50)
    private PerfilEnum perfil;

    @Enumerated(EnumType.STRING)
    @Column(name = "usuario_enum", nullable = false, length = 20)
    private UsuarioEnum status;

    public UsuarioEntity(Long id, String name, String login, String password,
                         PerfilEnum perfil, UsuarioEnum status) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.password = password;
        this.perfil = perfil;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UsuarioEntity outro)) {
            return false;
        }
        return id != null && Objects.equals(id, outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
