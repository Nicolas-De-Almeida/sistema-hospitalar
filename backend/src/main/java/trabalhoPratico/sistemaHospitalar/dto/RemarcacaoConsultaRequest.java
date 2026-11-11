package trabalhoPratico.sistemaHospitalar.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

public record RemarcacaoConsultaRequest(
        @NotNull(message = "A nova data é obrigatória.")
        @FutureOrPresent(message = "A consulta não pode ser remarcada para uma data passada.")
        LocalDate data,

        @NotNull(message = "O novo horário é obrigatório.")
        LocalTime horario) {
}
