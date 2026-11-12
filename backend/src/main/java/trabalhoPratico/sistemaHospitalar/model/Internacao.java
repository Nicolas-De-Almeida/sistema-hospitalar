package trabalhoPratico.sistemaHospitalar.model;

import static trabalhoPratico.sistemaHospitalar.util.Validacao.exigir;

import java.time.LocalDate;

import jakarta.persistence.*;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;
import trabalhoPratico.sistemaHospitalar.model.enums.SituacaoQuarto;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusInternacao;

// Decisão: Internacao é uma classe própria e NÃO uma modalidade de Atendimento
@Entity
@Table(name = "internacoes")
public class Internacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "profissional_id", nullable = false)
    private ProfissionalSaude profissional;

    @ManyToOne(optional = false)
    @JoinColumn(name = "quarto_id", nullable = false)
    private Quarto quarto;

    @Column(name = "data_entrada", nullable = false)
    private LocalDate dataEntrada;

    @Column(name = "data_prevista_alta", nullable = false)
    private LocalDate dataPrevistaAlta;

    @Column(name = "data_efetiva_alta")
    private LocalDate dataEfetivaAlta;

    @Column(length = 2000)
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatusInternacao status = StatusInternacao.ATIVA;

    protected Internacao() {
    }

    public Internacao(Paciente paciente, ProfissionalSaude profissional, Quarto quarto,
                      LocalDate dataEntrada, LocalDate dataPrevistaAlta, String observacoes) {
        this.paciente = exigir(paciente, "paciente");
        this.profissional = exigir(profissional, "profissional responsável");
        this.quarto = exigir(quarto, "quarto");
        this.dataEntrada = exigir(dataEntrada, "data de entrada");
        this.dataPrevistaAlta = exigir(dataPrevistaAlta, "data prevista de alta");
        this.observacoes = observacoes;

        if (dataPrevistaAlta.isBefore(dataEntrada)) {
            throw new RegraDeNegocioException("A data prevista de alta não pode ser antes da data de entrada.");
        }
        if (!profissional.isAtivo()) {
            throw new RegraDeNegocioException("O profissional " + profissional.getNome() + " está inativo.");
        }
        if (paciente.possuiInternacaoAtiva()) {
            throw new RegraDeNegocioException("O paciente " + paciente.getNome() + " já possui uma internação ativa.");
        }
        // Regra 4: o quarto precisa estar disponível
        if (quarto.situacao() != SituacaoQuarto.DISPONIVEL) {
            throw new RegraDeNegocioException("O quarto " + quarto.getNumero() + " não está disponível.");
        }

        // Só depois de tudo validado liga a internação ao quarto e ao paciente
        quarto.registrarInternacao(this); // também confere a capacidade (Regra 5)
        paciente.adicionarInternacao(this);
    }

    // ---------- Ações ----------

    // Registra a alta: encerra a internação e, com isso, libera a vaga no quarto
    public void darAlta(LocalDate dataAlta) {
        exigir(dataAlta, "data da alta");
        if (status != StatusInternacao.ATIVA) {
            throw new RegraDeNegocioException("Esta internação já foi encerrada.");
        }
        if (dataAlta.isBefore(dataEntrada)) {
            throw new RegraDeNegocioException("A data da alta não pode ser antes da data de entrada.");
        }
        this.dataEfetivaAlta = dataAlta;
        this.status = StatusInternacao.ENCERRADA;
    }

    public boolean estaAtiva() {
        return status == StatusInternacao.ATIVA;
    }

    // ---------- Getters e Setters ----------

    public Long getId() {
        return id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public ProfissionalSaude getProfissional() {
        return profissional;
    }

    public Quarto getQuarto() {
        return quarto;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public LocalDate getDataPrevistaAlta() {
        return dataPrevistaAlta;
    }

    public void setDataPrevistaAlta(LocalDate dataPrevistaAlta) {
        exigir(dataPrevistaAlta, "data prevista de alta");
        if (status != StatusInternacao.ATIVA) {
            throw new RegraDeNegocioException("Não é possível alterar uma internação encerrada.");
        }
        if (dataPrevistaAlta.isBefore(dataEntrada)) {
            throw new RegraDeNegocioException("A data prevista de alta não pode ser antes da data de entrada.");
        }
        this.dataPrevistaAlta = dataPrevistaAlta;
    }

    public LocalDate getDataEfetivaAlta() {
        return dataEfetivaAlta;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public StatusInternacao getStatus() {
        return status;
    }
}