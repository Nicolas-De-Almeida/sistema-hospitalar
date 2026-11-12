package trabalhoPratico.sistemaHospitalar.dto;

import jakarta.validation.constraints.NotNull;

public record InicioEmergenciaRequest(
        @NotNull(message = "O profissional responsável é obrigatório.")
        Long profissionalId) {
}
