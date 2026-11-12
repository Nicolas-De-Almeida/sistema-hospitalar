package trabalhoPratico.sistemaHospitalar.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import trabalhoPratico.sistemaHospitalar.model.enums.TipoQuarto;

public record QuartoRequest(
        @NotBlank(message = "O número do quarto é obrigatório.")
        @Size(max = 20, message = "O número do quarto deve ter no máximo 20 caracteres.")
        String numero,

        @Min(value = 0, message = "O andar não pode ser negativo.")
        int andar,

        @Positive(message = "A capacidade do quarto deve ser maior que zero.")
        int capacidadeMaxima,

        @NotNull(message = "O tipo do quarto é obrigatório.")
        TipoQuarto tipo) {
}
