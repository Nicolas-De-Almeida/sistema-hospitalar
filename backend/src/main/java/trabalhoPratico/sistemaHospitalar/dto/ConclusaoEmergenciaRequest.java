package trabalhoPratico.sistemaHospitalar.dto;

import jakarta.validation.constraints.Size;

public record ConclusaoEmergenciaRequest(
        @Size(max = 2000, message = "As observações devem ter no máximo 2000 caracteres.")
        String observacoes) {
}
