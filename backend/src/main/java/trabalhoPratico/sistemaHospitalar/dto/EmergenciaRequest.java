package trabalhoPratico.sistemaHospitalar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import trabalhoPratico.sistemaHospitalar.model.enums.PrioridadeEmergencia;

public record EmergenciaRequest(
        @NotNull(message = "O paciente é obrigatório.")
        Long pacienteId,

        @NotNull(message = "A prioridade é obrigatória.")
        PrioridadeEmergencia prioridade,

        @NotBlank(message = "A queixa principal é obrigatória.")
        @Size(max = 500, message = "A queixa principal deve ter no máximo 500 caracteres.")
        String queixaPrincipal) {
}
