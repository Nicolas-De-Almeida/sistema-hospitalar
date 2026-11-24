package trabalhoPratico.sistemaHospitalar.dto;

import java.time.LocalDateTime;

import trabalhoPratico.sistemaHospitalar.model.TransferenciaQuarto;

public record TransferenciaResponse(
        Long id,
        Long quartoOrigemId,
        String quartoOrigemNumero,
        Long quartoDestinoId,
        String quartoDestinoNumero,
        LocalDateTime dataHora) {

    public static TransferenciaResponse de(TransferenciaQuarto transferencia) {
        return new TransferenciaResponse(
                transferencia.getId(),
                transferencia.getQuartoOrigem().getId(),
                transferencia.getQuartoOrigem().getNumero(),
                transferencia.getQuartoDestino().getId(),
                transferencia.getQuartoDestino().getNumero(),
                transferencia.getDataHora());
    }
}