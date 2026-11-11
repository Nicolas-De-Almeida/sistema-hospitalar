package trabalhoPratico.sistemaHospitalar.model.enums;

public enum TipoRegistro {
    EVOLUCAO("Evolução"),
    DIAGNOSTICO("Diagnóstico"),
    PRESCRICAO("Prescrição"),
    OBSERVACAO("Observação");

    private final String descricao;

    TipoRegistro(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}