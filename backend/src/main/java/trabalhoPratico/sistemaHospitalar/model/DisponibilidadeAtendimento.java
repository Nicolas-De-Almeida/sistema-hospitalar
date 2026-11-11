package trabalhoPratico.sistemaHospitalar.model;

import static trabalhoPratico.sistemaHospitalar.util.Validacao.exigir;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.*;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;

// "Expediente" de um profissional: ex. segundas, das 08:00 às 12:00, consultas de 30 min.
// Um profissional pode ter várias.
@Entity
@Table(name = "disponibilidades_atendimento")
public class DisponibilidadeAtendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "profissional_id", nullable = false)
    private ProfissionalSaude profissional;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 12)
    private DayOfWeek diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim;

    @Column(name = "duracao_consulta_min", nullable = false)
    private int duracaoConsultaMin;

    protected DisponibilidadeAtendimento() {
    }

    // Sem "public": quem cria é o ProfissionalSaude.adicionarDisponibilidade
    DisponibilidadeAtendimento(ProfissionalSaude profissional, DayOfWeek diaSemana,
                               LocalTime horaInicio, LocalTime horaFim, int duracaoConsultaMin) {
        this.profissional = exigir(profissional, "profissional");
        this.diaSemana = exigir(diaSemana, "dia da semana");
        this.horaInicio = exigir(horaInicio, "hora de início");
        this.horaFim = exigir(horaFim, "hora de fim");
        if (!horaFim.isAfter(horaInicio)) {
            throw new RegraDeNegocioException("A hora de fim deve ser depois da hora de início.");
        }
        if (duracaoConsultaMin <= 0) {
            throw new RegraDeNegocioException("A duração da consulta deve ser maior que zero.");
        }
        this.duracaoConsultaMin = duracaoConsultaMin;
    }

    // Esse expediente cobre a data/hora informada?
    public boolean cobre(LocalDate data, LocalTime hora) {
        return data.getDayOfWeek() == diaSemana
                && !hora.isBefore(horaInicio)
                && hora.isBefore(horaFim);
    }

    // Dois expedientes do mesmo dia não podem se sobrepor
    boolean sobrepoe(DayOfWeek outroDia, LocalTime outroInicio, LocalTime outroFim) {
        return diaSemana == outroDia && horaInicio.isBefore(outroFim) && outroInicio.isBefore(horaFim);
    }

    public Long getId() {
        return id;
    }

    public ProfissionalSaude getProfissional() {
        return profissional;
    }

    public DayOfWeek getDiaSemana() {
        return diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public int getDuracaoConsultaMin() {
        return duracaoConsultaMin;
    }

    public void setDuracaoConsultaMin(int duracaoConsultaMin) {
        if (duracaoConsultaMin <= 0) {
            throw new RegraDeNegocioException("A duração da consulta deve ser maior que zero.");
        }
        this.duracaoConsultaMin = duracaoConsultaMin;
    }
}