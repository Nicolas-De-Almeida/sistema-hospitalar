package trabalhoPratico.sistemaHospitalar.dto;

import jakarta.validation.constraints.NotNull;
import trabalhoPratico.sistemaHospitalar.model.enums.PrioridadeEmergencia;

public record PrioridadeEmergenciaRequest(
        @NotNull(message = "A prioridade é obrigatória.")
        PrioridadeEmergencia prioridade) {
}
