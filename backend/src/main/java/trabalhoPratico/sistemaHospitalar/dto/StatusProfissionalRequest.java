package trabalhoPratico.sistemaHospitalar.dto;

import jakarta.validation.constraints.NotNull;

public record StatusProfissionalRequest(
        @NotNull(message = "O status ativo é obrigatório.")
        Boolean ativo) {
}
