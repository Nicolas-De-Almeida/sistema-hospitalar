package trabalhoPratico.sistemaHospitalar.model;

import static trabalhoPratico.sistemaHospitalar.util.Validacao.exigir;
import static trabalhoPratico.sistemaHospitalar.util.Validacao.exigirTexto;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import trabalhoPratico.sistemaHospitalar.model.enums.TipoRegistro;

// Informações relevantes registradas durante os atendimentos e criado através de atendimento.registrar(tipo, descricao).
@Entity
@Table(name = "registros_clinicos")
public class RegistroClinico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "atendimento_id", nullable = false)
    private Atendimento atendimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoRegistro tipo;

    @Column(name = "data_hora", nullable = false, updatable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    @Column(nullable = false, length = 2000)
    private String descricao;

    protected RegistroClinico() {
    }

    RegistroClinico(Atendimento atendimento, TipoRegistro tipo, String descricao) {
        this.atendimento = exigir(atendimento, "atendimento");
        this.tipo = exigir(tipo, "tipo do registro");
        this.descricao = exigirTexto(descricao, "descrição");
    }

    public Long getId() {
        return id;
    }

    public Atendimento getAtendimento() {
        return atendimento;
    }

    public TipoRegistro getTipo() {
        return tipo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = exigirTexto(descricao, "descrição");
    }
}