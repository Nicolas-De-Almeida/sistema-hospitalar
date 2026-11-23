package trabalhoPratico.sistemaHospitalar.dto;

import jakarta.validation.constraints.NotNull;

public record TransferenciaInternacaoRequest(
        @NotNull(message = "O novo quarto é obrigatório.")
        Long quartoId) {
}