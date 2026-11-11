package trabalhoPratico.sistemaHospitalar.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import trabalhoPratico.sistemaHospitalar.model.enums.Especialidade;
import trabalhoPratico.sistemaHospitalar.model.enums.ModalidadeConsulta;

public record ConsultaRequest(
        @NotNull(message = "O paciente é obrigatório.")
        Long pacienteId,

        @NotNull(message = "O profissional é obrigatório.")
        Long profissionalId,

        @NotNull(message = "A modalidade da consulta é obrigatória.")
        ModalidadeConsulta modalidade,

        @NotNull(message = "A data da consulta é obrigatória.")
        @FutureOrPresent(message = "A consulta não pode ser agendada para uma data passada.")
        LocalDate data,

        @NotNull(message = "O horário da consulta é obrigatório.")
        LocalTime horario,

        @NotBlank(message = "O motivo da consulta é obrigatório.")
        @Size(max = 255, message = "O motivo deve ter no máximo 255 caracteres.")
        String motivo,

        Especialidade especialidade) {
}
