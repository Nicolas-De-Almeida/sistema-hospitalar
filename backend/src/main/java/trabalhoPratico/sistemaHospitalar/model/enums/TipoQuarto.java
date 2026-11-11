package trabalhoPratico.sistemaHospitalar.model.enums;

public enum TipoQuarto {
    ENFERMARIA("Enfermaria", 6),
    APARTAMENTO("Apartamento", 2),
    UTI("UTI", 1),
    ISOLAMENTO("Isolamento", 1);

    private final String descricao;
    private final int limiteDePacientes; // máximo de leitos que esse tipo de quarto pode ter

    TipoQuarto(String descricao, int limiteDePacientes) {
        this.descricao = descricao;
        this.limiteDePacientes = limiteDePacientes;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getLimiteDePacientes() {
        return limiteDePacientes;
    }
}