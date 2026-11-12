package trabalhoPratico.sistemaHospitalar.model.enums;

public enum StatusEmergencia {
    AGUARDANDO_AVALIACAO("Aguardando avaliação"),
    EM_ATENDIMENTO("Em atendimento"),
    CONCLUIDO("Concluído");

    private final String descricao;

    StatusEmergencia(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean podeIrPara(StatusEmergencia destino) {
        return switch (this) {
            case AGUARDANDO_AVALIACAO -> destino == EM_ATENDIMENTO;
            case EM_ATENDIMENTO -> destino == CONCLUIDO;
            case CONCLUIDO -> false;
        };
    }
}