package trabalhoPratico.sistemaHospitalar.dto;

import java.time.LocalDate;

import trabalhoPratico.sistemaHospitalar.model.Internacao;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusInternacao;

public record InternacaoResponse(
        Long id,
        Long pacienteId,
        String pacienteNome,
        Long profissionalId,
        String profissionalNome,
        Long quartoId,
        String quartoNumero,
        LocalDate dataEntrada,
        LocalDate dataPrevistaAlta,
        LocalDate dataEfetivaAlta,
        String observacoes,
        StatusInternacao status) {

    public static InternacaoResponse de(Internacao internacao) {
        return new InternacaoResponse(
                internacao.getId(),
                internacao.getPaciente().getId(),
                internacao.getPaciente().getNome(),
                internacao.getProfissional().getId(),
                internacao.getProfissional().getNome(),
                internacao.getQuarto().getId(),
                internacao.getQuarto().getNumero(),
                internacao.getDataEntrada(),
                internacao.getDataPrevistaAlta(),
                internacao.getDataEfetivaAlta(),
                internacao.getObservacoes(),
                internacao.getStatus());
    }
}
