package trabalhoPratico.sistemaHospitalar.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InternacaoRequest(
        @NotNull(message = "O paciente é obrigatório.")
        Long pacienteId,

        @NotNull(message = "O profissional responsável é obrigatório.")
        Long profissionalId,

        @NotNull(message = "O quarto é obrigatório.")
        Long quartoId,

        @NotNull(message = "A data de entrada é obrigatória.")
        LocalDate dataEntrada,

        @NotNull(message = "A data prevista de alta é obrigatória.")
        LocalDate dataPrevistaAlta,

        @Size(max = 2000, message = "As observações devem ter no máximo 2000 caracteres.")
        String observacoes) {
}
