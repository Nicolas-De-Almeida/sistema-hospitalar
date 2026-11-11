package trabalhoPratico.sistemaHospitalar.model;

public enum Perfil {
    ADMINISTRADOR("Administrador"),
    PROFISSIONAL_SAUDE("Profissional de saúde"),
    RECEPCIONISTA("Recepcionista");

    private final String descricao;

    Perfil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
