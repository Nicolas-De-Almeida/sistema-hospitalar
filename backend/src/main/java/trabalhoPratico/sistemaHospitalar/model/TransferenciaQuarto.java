package trabalhoPratico.sistemaHospitalar.model;

import static trabalhoPratico.sistemaHospitalar.util.Validacao.exigir;

import java.time.LocalDateTime;

import jakarta.persistence.*;

// Registro de uma troca de quarto: de qual quarto, para qual, em que dia e hora.
@Entity
@Table(name = "transferencias_quarto")
public class TransferenciaQuarto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "internacao_id", nullable = false)
    private Internacao internacao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "quarto_origem_id", nullable = false)
    private Quarto quartoOrigem;

    @ManyToOne(optional = false)
    @JoinColumn(name = "quarto_destino_id", nullable = false)
    private Quarto quartoDestino;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    protected TransferenciaQuarto() {
    }

    // Só a Internacao cria transferências (quando o paciente muda de quarto)
    TransferenciaQuarto(Internacao internacao, Quarto quartoOrigem, Quarto quartoDestino) {
        this.internacao = exigir(internacao, "internação");
        this.quartoOrigem = exigir(quartoOrigem, "quarto de origem");
        this.quartoDestino = exigir(quartoDestino, "quarto de destino");
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Internacao getInternacao() { return internacao; }
    public Quarto getQuartoOrigem() { return quartoOrigem; }
    public Quarto getQuartoDestino() { return quartoDestino; }
    public LocalDateTime getDataHora() { return dataHora; }
}