package br.com.project_sena.adapter.in.controller.request.ocorrencia;


public record RelatorioOcorrenciaDTO(
        Long ocorrenciaId,
        Long classId,
        Long studentId,
        String categoria
) {
}
