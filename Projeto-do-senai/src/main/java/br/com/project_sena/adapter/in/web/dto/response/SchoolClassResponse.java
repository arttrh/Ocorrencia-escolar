package br.com.project_sena.adapter.in.web.dto.response;

import br.com.project_sena.application.core.domain.enums.SemestreEnum;
import br.com.project_sena.application.core.domain.enums.TurmaTurnoEnum;

public record SchoolClassResponse(Long id,
                                  String name,
                                  TurmaTurnoEnum shift,
                                  Integer year,
                                  SemestreEnum semester,
                                  boolean canceled) {
}
