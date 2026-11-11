package trabalhoPratico.sistemaHospitalar.dto;

import jakarta.validation.constraints.Size;

public record ConclusaoProcedimentoRequest(
        @Size(max = 2000, message = "As observações pós-operatórias devem ter no máximo 2000 caracteres.")
        String observacoesPosOperatorias) {
}
