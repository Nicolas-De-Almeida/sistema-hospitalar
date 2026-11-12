package trabalhoPratico.sistemaHospitalar.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DisponibilidadeRequest(
        @NotNull(message = "O dia da semana é obrigatório.")
        DayOfWeek diaSemana,

        @NotNull(message = "A hora de início é obrigatória.")
        LocalTime horaInicio,

        @NotNull(message = "A hora de fim é obrigatória.")
        LocalTime horaFim,

        @Positive(message = "A duração da consulta deve ser maior que zero.")
        int duracaoConsultaMin) {
}
