package br.com.project_sena.adapter.in.web.dto.response;

import java.util.List;

import br.com.project_sena.application.core.domain.vo.ContagemPorChave;

/**
 * Dados do dashboard de ocorrencias.
 *
 * <p>Os nomes dos totais sao os mesmos que o {@code dashboard-ocorrencias.js} le
 * ({@code active}, {@code waiting}, {@code progressing}, {@code solved}, {@code unsolved},
 * {@code closed}) e cada agregacao vem como lista de {@code {key, value}}, formato que o
 * script ja sabe processar.</p>
 */
public record IncidentSummaryResponse(long active,
                                      long waiting,
                                      long progressing,
                                      long solved,
                                      long unsolved,
                                      long closed,
                                      long total,
                                      List<ContagemPorChave> byCategory,
                                      List<ContagemPorChave> byType,
                                      List<ContagemPorChave> bySchoolClass,
                                      List<ContagemPorChave> byStudent) {
}
