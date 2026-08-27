package br.com.project_sena.application.core.domain.vo;

import java.util.List;
import java.util.Map;

import br.com.project_sena.application.core.domain.enums.OcorrenciaEnum;

/**
 * Numeros consolidados do dashboard de ocorrencias.
 *
 * <p>Os totais por situacao sao derivados do mapa recebido para que incluir um novo
 * status no {@link OcorrenciaEnum} nao exija mudar esta classe.</p>
 */
public record ResumoOcorrencias(
        Map<OcorrenciaEnum, Long> porStatus,
        List<ContagemPorChave> byCategory,
        List<ContagemPorChave> byType,
        List<ContagemPorChave> bySchoolClass,
        List<ContagemPorChave> byStudent) {

    public ResumoOcorrencias {
        porStatus = porStatus == null ? Map.of() : Map.copyOf(porStatus);
        byCategory = byCategory == null ? List.of() : List.copyOf(byCategory);
        byType = byType == null ? List.of() : List.copyOf(byType);
        bySchoolClass = bySchoolClass == null ? List.of() : List.copyOf(bySchoolClass);
        byStudent = byStudent == null ? List.of() : List.copyOf(byStudent);
    }

    public long total(OcorrenciaEnum status) {
        return porStatus.getOrDefault(status, 0L);
    }

    public long totalGeral() {
        return porStatus.values().stream().mapToLong(Long::longValue).sum();
    }
}
