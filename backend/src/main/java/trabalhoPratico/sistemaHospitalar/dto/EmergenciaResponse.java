package trabalhoPratico.sistemaHospitalar.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import trabalhoPratico.sistemaHospitalar.model.AtendimentoEmergencia;
import trabalhoPratico.sistemaHospitalar.model.enums.PrioridadeEmergencia;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusEmergencia;

public record EmergenciaResponse(
        Long id,
        Long pacienteId,
        String pacienteNome,
        Long profissionalId,
        String profissionalNome,
        LocalDate dataChegada,
        LocalTime horarioChegada,
        PrioridadeEmergencia prioridade,
        String queixaPrincipal,
        String observacoes,
        StatusEmergencia status,
        String situacao) {

    public static EmergenciaResponse de(AtendimentoEmergencia emergencia) {
        return new EmergenciaResponse(
                emergencia.getId(),
                emergencia.getPaciente().getId(),
                emergencia.getPaciente().getNome(),
                emergencia.getProfissional() == null ? null : emergencia.getProfissional().getId(),
                emergencia.getProfissional() == null ? null : emergencia.getProfissional().getNome(),
                emergencia.getData(),
                emergencia.getHorario(),
                emergencia.getPrioridade(),
                emergencia.getQueixaPrincipal(),
                emergencia.getObservacoes(),
                emergencia.getStatus(),
                emergencia.getSituacao());
    }
}
