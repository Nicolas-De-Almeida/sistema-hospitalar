package trabalhoPratico.sistemaHospitalar.dto;

import static trabalhoPratico.sistemaHospitalar.util.Validacao.exigir;

import java.util.Comparator;
import java.util.List;

import trabalhoPratico.sistemaHospitalar.model.Atendimento;
import trabalhoPratico.sistemaHospitalar.model.Consulta;
import trabalhoPratico.sistemaHospitalar.model.Internacao;
import trabalhoPratico.sistemaHospitalar.model.Paciente;
import trabalhoPratico.sistemaHospitalar.model.RegistroClinico;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusConsulta;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusInternacao;

// Consultas realizadas, internações realizadas e os registros clínicos.
public final class HistoricoMedico {

    private final Paciente paciente;
    private final List<Consulta> consultas;
    private final List<Internacao> internacoes;
    private final List<RegistroClinico> registros;

    public HistoricoMedico(Paciente paciente) {
        this.paciente = exigir(paciente, "paciente");

        this.consultas = paciente.getConsultas().stream()
                .filter(c -> c.getStatus() == StatusConsulta.CONCLUIDA)
                .sorted(Comparator.comparing(Consulta::getData).thenComparing(Consulta::getHorario))
                .toList();

        this.internacoes = paciente.getInternacoes().stream()
                .filter(i -> i.getStatus() == StatusInternacao.ENCERRADA)
                .sorted(Comparator.comparing(Internacao::getDataEntrada))
                .toList();

        this.registros = paciente.getAtendimentos().stream()
                .map(Atendimento::getRegistros)
                .flatMap(List::stream)
                .sorted(Comparator.comparing(RegistroClinico::getDataHora))
                .toList();
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public List<Consulta> getConsultas() {
        return consultas;
    }

    public List<Internacao> getInternacoes() {
        return internacoes;
    }

    public List<RegistroClinico> getRegistros() {
        return registros;
    }
}