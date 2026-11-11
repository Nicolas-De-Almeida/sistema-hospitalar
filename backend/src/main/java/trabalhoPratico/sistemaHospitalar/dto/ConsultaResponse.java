package trabalhoPratico.sistemaHospitalar.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import trabalhoPratico.sistemaHospitalar.model.Consulta;
import trabalhoPratico.sistemaHospitalar.model.ConsultaEspecializada;
import trabalhoPratico.sistemaHospitalar.model.enums.Especialidade;
import trabalhoPratico.sistemaHospitalar.model.enums.ModalidadeConsulta;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusConsulta;

public record ConsultaResponse(
        Long id,
        Long pacienteId,
        String pacienteNome,
        Long profissionalId,
        String profissionalNome,
        ModalidadeConsulta modalidade,
        Especialidade especialidade,
        LocalDate data,
        LocalTime horario,
        String motivo,
        String observacoes,
        StatusConsulta status,
        String situacao) {

    public static ConsultaResponse de(Consulta consulta) {
        boolean especializada = consulta instanceof ConsultaEspecializada;
        return new ConsultaResponse(
                consulta.getId(),
                consulta.getPaciente().getId(),
                consulta.getPaciente().getNome(),
                consulta.getProfissional().getId(),
                consulta.getProfissional().getNome(),
                especializada ? ModalidadeConsulta.ESPECIALIZADA : ModalidadeConsulta.ROTINA,
                especializada ? ((ConsultaEspecializada) consulta).getEspecialidade() : null,
                consulta.getData(),
                consulta.getHorario(),
                consulta.getMotivo(),
                consulta.getObservacoes(),
                consulta.getStatus(),
                consulta.getSituacao());
    }
}
