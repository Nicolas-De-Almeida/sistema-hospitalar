package trabalhoPratico.sistemaHospitalar.util;

import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;

// Pequenas verificações reaproveitadas pelas classes do modelo, para não repetir "if (x == null) throw ..." em todo lugar ;)
public final class Validacao {

    private Validacao() {
    }

    public static String exigirTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new RegraDeNegocioException("O campo '" + campo + "' é obrigatório.");
        }
        return valor.trim();
    }

    public static <T> T exigir(T valor, String campo) {
        if (valor == null) {
            throw new RegraDeNegocioException("O campo '" + campo + "' é obrigatório.");
        }
        return valor;
    }
}