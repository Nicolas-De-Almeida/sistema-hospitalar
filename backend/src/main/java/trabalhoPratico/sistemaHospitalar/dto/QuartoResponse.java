package trabalhoPratico.sistemaHospitalar.dto;

import trabalhoPratico.sistemaHospitalar.model.Quarto;
import trabalhoPratico.sistemaHospitalar.model.enums.SituacaoQuarto;
import trabalhoPratico.sistemaHospitalar.model.enums.TipoQuarto;

public record QuartoResponse(
        Long id,
        String numero,
        int andar,
        int capacidadeMaxima,
        TipoQuarto tipo,
        int ocupacaoAtual,
        int vagasDisponiveis,
        SituacaoQuarto situacao) {

    public static QuartoResponse de(Quarto quarto) {
        int ocupacao = quarto.ocupacaoAtual();
        return new QuartoResponse(
                quarto.getId(),
                quarto.getNumero(),
                quarto.getAndar(),
                quarto.getCapacidadeMaxima(),
                quarto.getTipo(),
                ocupacao,
                quarto.getCapacidadeMaxima() - ocupacao,
                quarto.situacao());
    }
}
