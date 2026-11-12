package trabalhoPratico.sistemaHospitalar.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record AltaInternacaoRequest(
        @NotNull(message = "A data da alta é obrigatória.")
        LocalDate dataAlta) {
}
