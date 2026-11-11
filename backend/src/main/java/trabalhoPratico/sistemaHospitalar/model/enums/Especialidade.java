package trabalhoPratico.sistemaHospitalar.model.enums;

public enum Especialidade {
    CLINICO_GERAL("Clínico geral"),
    CARDIOLOGIA("Cardiologia"),
    NEUROLOGIA("Neurologia"),
    ORTOPEDIA("Ortopedia"),
    PEDIATRIA("Pediatria"),
    CIRURGIA_GERAL("Cirurgia geral"),
    OUTRA("Outra");

    private final String descricao;

    Especialidade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}