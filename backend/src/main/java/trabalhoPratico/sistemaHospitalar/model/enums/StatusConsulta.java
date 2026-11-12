package trabalhoPratico.sistemaHospitalar.model.enums;

public enum StatusConsulta {
    AGENDADA("Agendada"),
    CONFIRMADA("Confirmada"),
    EM_ATENDIMENTO("Em atendimento"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada"),
    NAO_COMPARECEU("Não compareceu");

    private final String descricao;

    StatusConsulta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    // Mapa das mudanças permitidas. Tudo que não está aqui é proibido.
    public boolean podeIrPara(StatusConsulta destino) {
        return switch (this) {
            case AGENDADA -> destino == CONFIRMADA || destino == EM_ATENDIMENTO
                    || destino == CANCELADA || destino == NAO_COMPARECEU;
            case CONFIRMADA -> destino == EM_ATENDIMENTO
                    || destino == CANCELADA || destino == NAO_COMPARECEU;
            case EM_ATENDIMENTO -> destino == CONCLUIDA;
            case CONCLUIDA, CANCELADA, NAO_COMPARECEU -> false;
        };
    }

    // Situações das quais não se sai mais (só com um NOVO agendamento)
    public boolean isFinal() {
        return this == CONCLUIDA || this == CANCELADA || this == NAO_COMPARECEU;
    }
}