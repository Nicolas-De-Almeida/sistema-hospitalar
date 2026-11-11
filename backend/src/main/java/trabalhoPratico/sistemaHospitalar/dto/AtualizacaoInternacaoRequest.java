package trabalhoPratico.sistemaHospitalar.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AtualizacaoInternacaoRequest(
        @NotNull(message = "A data prevista de alta é obrigatória.")
        LocalDate dataPrevistaAlta,

        @Size(max = 2000, message = "As observações devem ter no máximo 2000 caracteres.")
        String observacoes) {
}
