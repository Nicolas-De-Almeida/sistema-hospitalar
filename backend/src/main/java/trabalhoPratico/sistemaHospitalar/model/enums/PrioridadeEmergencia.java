package trabalhoPratico.sistemaHospitalar.model.enums;

public enum PrioridadeEmergencia {
    BAIXA("Baixa", 1),
    MODERADA("Moderada", 2),
    ALTA("Alta", 3),
    CRITICA("Crítica", 4);

    private final String descricao;
    private final int nivel; // quanto maior, mais urgente (útil para ordenar a fila)

    PrioridadeEmergencia(String descricao, int nivel) {
        this.descricao = descricao;
        this.nivel = nivel;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getNivel() {
        return nivel;
    }
}