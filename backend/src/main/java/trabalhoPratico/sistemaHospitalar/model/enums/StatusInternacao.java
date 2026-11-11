package trabalhoPratico.sistemaHospitalar.model.enums;

public enum StatusInternacao {
    ATIVA("Ativa"),
    ENCERRADA("Encerrada");

    private final String descricao;

    StatusInternacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}