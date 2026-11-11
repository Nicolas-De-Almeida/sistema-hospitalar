package trabalhoPratico.sistemaHospitalar.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

import trabalhoPratico.sistemaHospitalar.model.DisponibilidadeAtendimento;

public record DisponibilidadeResponse(
        Long id,
        DayOfWeek diaSemana,
        LocalTime horaInicio,
        LocalTime horaFim,
        int duracaoConsultaMin) {

    public static DisponibilidadeResponse de(DisponibilidadeAtendimento disponibilidade) {
        return new DisponibilidadeResponse(
                disponibilidade.getId(),
                disponibilidade.getDiaSemana(),
                disponibilidade.getHoraInicio(),
                disponibilidade.getHoraFim(),
                disponibilidade.getDuracaoConsultaMin());
    }
}
