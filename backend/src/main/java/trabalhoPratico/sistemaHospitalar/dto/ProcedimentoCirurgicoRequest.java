package trabalhoPratico.sistemaHospitalar.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import trabalhoPratico.sistemaHospitalar.model.enums.TipoCirurgia;

public record ProcedimentoCirurgicoRequest(
        @NotNull(message = "O paciente é obrigatório.")
        Long pacienteId,

        @NotNull(message = "O cirurgião é obrigatório.")
        Long cirurgiaoId,

        @NotNull(message = "A data prevista é obrigatória.")
        @FutureOrPresent(message = "O procedimento não pode ser agendado para uma data passada.")
        LocalDate dataPrevista,

        @NotNull(message = "O horário previsto é obrigatório.")
        LocalTime horarioPrevisto,

        @NotBlank(message = "A descrição do procedimento é obrigatória.")
        @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres.")
        String descricao,

        @NotNull(message = "O tipo de cirurgia é obrigatório.")
        TipoCirurgia tipoCirurgia,

        @Size(max = 2000, message = "As observações pré-operatórias devem ter no máximo 2000 caracteres.")
        String observacoesPreOperatorias) {
}
