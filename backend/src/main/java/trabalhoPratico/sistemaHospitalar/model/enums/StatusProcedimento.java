package trabalhoPratico.sistemaHospitalar.model.enums;

public enum StatusProcedimento {
    AGENDADO("Agendado"),
    EM_ANDAMENTO("Em andamento"),
    CONCLUIDO("Concluído"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusProcedimento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean podeIrPara(StatusProcedimento destino) {
        return switch (this) {
            case AGENDADO -> destino == EM_ANDAMENTO || destino == CANCELADO;
            case EM_ANDAMENTO -> destino == CONCLUIDO; // só conclui quando for iniciado
            case CONCLUIDO, CANCELADO -> false;
        };
    }
}