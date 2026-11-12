package trabalhoPratico.sistemaHospitalar.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import trabalhoPratico.sistemaHospitalar.model.ProcedimentoCirurgico;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusProcedimento;
import trabalhoPratico.sistemaHospitalar.model.enums.TipoCirurgia;

public record ProcedimentoCirurgicoResponse(
        Long id,
        Long pacienteId,
        String pacienteNome,
        Long cirurgiaoId,
        String cirurgiaoNome,
        LocalDate dataPrevista,
        LocalTime horarioPrevisto,
        LocalDate dataEfetiva,
        LocalTime horarioEfetivo,
        String descricao,
        TipoCirurgia tipoCirurgia,
        String observacoesPreOperatorias,
        String observacoesPosOperatorias,
        StatusProcedimento status,
        String situacao) {

    public static ProcedimentoCirurgicoResponse de(ProcedimentoCirurgico procedimento) {
        return new ProcedimentoCirurgicoResponse(
                procedimento.getId(),
                procedimento.getPaciente().getId(),
                procedimento.getPaciente().getNome(),
                procedimento.getCirurgiao().getId(),
                procedimento.getCirurgiao().getNome(),
                procedimento.getDataPrevista(),
                procedimento.getHorarioPrevisto(),
                procedimento.getDataEfetiva(),
                procedimento.getHorarioEfetivo(),
                procedimento.getDescricao(),
                procedimento.getTipoCirurgia(),
                procedimento.getObservacoesPreOperatorias(),
                procedimento.getObservacoesPosOperatorias(),
                procedimento.getStatus(),
                procedimento.getSituacao());
    }
}
