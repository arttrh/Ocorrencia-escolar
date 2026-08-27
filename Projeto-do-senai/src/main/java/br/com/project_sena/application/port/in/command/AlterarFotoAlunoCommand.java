package br.com.project_sena.application.port.in.command;

/** @param imageUrl a imagem ja resolvida pelo adaptador (data URI ou URL externa) */
public record AlterarFotoAlunoCommand(Long id, String imageUrl) {
}
