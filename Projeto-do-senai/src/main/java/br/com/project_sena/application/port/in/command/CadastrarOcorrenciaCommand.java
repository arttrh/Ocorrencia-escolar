package br.com.project_sena.application.port.in.command;

import java.time.LocalDateTime;

/**
 * @param category nome da categoria (o front envia o nome vindo de /incidents/categories)
 * @param type     nome do tipo (o front envia o nome vindo de /incidents/types/{category})
 */
public record CadastrarOcorrenciaCommand(Long schoolClassId,
                                         Long studentId,
                                         String category,
                                         String type,
                                         LocalDateTime registerDate,
                                         String description) {
}
