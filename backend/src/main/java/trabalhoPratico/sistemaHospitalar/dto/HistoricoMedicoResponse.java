package trabalhoPratico.sistemaHospitalar.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

import trabalhoPratico.sistemaHospitalar.model.Consulta;
import trabalhoPratico.sistemaHospitalar.model.Internacao;
import trabalhoPratico.sistemaHospitalar.model.Paciente;
import trabalhoPratico.sistemaHospitalar.model.RegistroClinico;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusConsulta;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusInternacao;
import trabalhoPratico.sistemaHospitalar.model.enums.TipoRegistro;

public record HistoricoMedicoResponse(
        PacienteResponse paciente,
        List<ConsultaHistoricoResponse> consultas,
        List<InternacaoHistoricoResponse> internacoes,
        List<RegistroClinicoResponse> registros) {

    public static HistoricoMedicoResponse de(Paciente paciente) {
        List<ConsultaHistoricoResponse> consultas = paciente.getConsultas().stream()
                .filter(consulta -> consulta.getStatus() == StatusConsulta.CONCLUIDA)
                .sorted(Comparator.comparing(Consulta::getData).thenComparing(Consulta::getHorario))
                .map(ConsultaHistoricoResponse::de)
                .toList();

        List<InternacaoHistoricoResponse> internacoes = paciente.getInternacoes().stream()
                .filter(internacao -> internacao.getStatus() == StatusInternacao.ENCERRADA)
                .sorted(Comparator.comparing(Internacao::getDataEntrada))
                .map(InternacaoHistoricoResponse::de)
                .toList();

        List<RegistroClinicoResponse> registros = paciente.getAtendimentos().stream()
                .flatMap(atendimento -> atendimento.getRegistros().stream())
                .sorted(Comparator.comparing(RegistroClinico::getDataHora))
                .map(RegistroClinicoResponse::de)
                .toList();

        return new HistoricoMedicoResponse(PacienteResponse.de(paciente), consultas, internacoes, registros);
    }

    public record ConsultaHistoricoResponse(
            Long id,
            String modalidade,
            LocalDate data,
            LocalTime horario,
            String profissional,
            String motivo,
            String observacoes,
            StatusConsulta status) {

        static ConsultaHistoricoResponse de(Consulta consulta) {
            return new ConsultaHistoricoResponse(
                    consulta.getId(),
                    consulta.getClass().getSimpleName(),
                    consulta.getData(),
                    consulta.getHorario(),
                    consulta.getProfissional() == null ? null : consulta.getProfissional().getNome(),
                    consulta.getMotivo(),
                    consulta.getObservacoes(),
                    consulta.getStatus());
        }
    }

    public record InternacaoHistoricoResponse(
            Long id,
            LocalDate dataEntrada,
            LocalDate dataPrevistaAlta,
            LocalDate dataEfetivaAlta,
            String profissional,
            String quarto,
            String observacoes,
            StatusInternacao status) {

        static InternacaoHistoricoResponse de(Internacao internacao) {
            return new InternacaoHistoricoResponse(
                    internacao.getId(),
                    internacao.getDataEntrada(),
                    internacao.getDataPrevistaAlta(),
                    internacao.getDataEfetivaAlta(),
                    internacao.getProfissional().getNome(),
                    internacao.getQuarto().getNumero(),
                    internacao.getObservacoes(),
                    internacao.getStatus());
        }
    }

    public record RegistroClinicoResponse(
            Long id,
            Long atendimentoId,
            TipoRegistro tipo,
            LocalDateTime dataHora,
            String descricao) {

        static RegistroClinicoResponse de(RegistroClinico registro) {
            return new RegistroClinicoResponse(
                    registro.getId(),
                    registro.getAtendimento().getId(),
                    registro.getTipo(),
                    registro.getDataHora(),
                    registro.getDescricao());
        }
    }
}
