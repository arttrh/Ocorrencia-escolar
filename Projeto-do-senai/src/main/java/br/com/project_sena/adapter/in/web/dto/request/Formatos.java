package br.com.project_sena.adapter.in.web.dto.request;

/**
 * Padroes de data usados no contrato HTTP.
 *
 * <p>Sao exatamente os formatos que o front-end ja monta nos formularios; centralizados
 * aqui para que serializacao e desserializacao nao saiam de sincronia.</p>
 */
public final class Formatos {

    /** Ex.: {@code 27/03/2008} */
    public static final String DATA = "dd/MM/yyyy";

    /** Ex.: {@code 27/03/2008 - 14:30} */
    public static final String DATA_HORA = "dd/MM/yyyy - HH:mm";

    private Formatos() {
    }
}
