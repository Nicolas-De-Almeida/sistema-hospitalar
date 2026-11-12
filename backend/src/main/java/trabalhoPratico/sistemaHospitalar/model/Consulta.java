package trabalhoPratico.sistemaHospitalar.model;

import static trabalhoPratico.sistemaHospitalar.util.Validacao.exigirTexto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.*;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusConsulta;

// Parte comum às duas consultas (rotina e especializada)
// O status NÃO tem setter de propósito (só muda pelos métodos abaixo), que respeitam as transições permitidas em StatusConsulta.
@Entity
public abstract class Consulta extends Atendimento {

    @Column(length = 255)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_consulta", length = 20)
    private StatusConsulta status = StatusConsulta.AGENDADA;

    protected Consulta() {
    }

    protected Consulta(Paciente paciente, ProfissionalSaude profissional,
                       LocalDate data, LocalTime horario, String motivo) {
        super(paciente, profissional, data, horario);
        this.motivo = exigirTexto(motivo, "motivo da consulta");
    }

    // ---------- Ações que mudam a situação ----------

    public void confirmar() {
        transitar(StatusConsulta.CONFIRMADA);
    }

    public void iniciar() {
        validarCondicoes();
        transitar(StatusConsulta.EM_ATENDIMENTO);
    }

    // Conclui e registra as observações médicas
    public void concluir(String observacoes) {
        transitar(StatusConsulta.CONCLUIDA);
        if (observacoes != null && !observacoes.isBlank()) {
            setObservacoes(observacoes.trim());
        }
    }

    public void cancelar() {
        transitar(StatusConsulta.CANCELADA);
    }

    public void registrarFalta() {
        transitar(StatusConsulta.NAO_COMPARECEU);
    }

    // Só consultas ainda não iniciadas podem mudar de data/horário; volta para AGENDADA
    public void remarcar(LocalDate novaData, LocalTime novoHorario) {
        if (status != StatusConsulta.AGENDADA && status != StatusConsulta.CONFIRMADA) {
            throw new RegraDeNegocioException("Só é possível remarcar consultas agendadas ou confirmadas "
                    + "(situação atual: " + status.getDescricao() + ").");
        }
        LocalDate dataAntiga = getData();
        LocalTime horarioAntigo = getHorario();
        alterarDataHorario(novaData, novoHorario);
        try {
            validarAgendamento();
        } catch (RuntimeException erro) {
            alterarDataHorario(dataAntiga, horarioAntigo); // desfaz se o novo horário não serve
            throw erro;
        }
        status = StatusConsulta.AGENDADA;
    }

    private void transitar(StatusConsulta novo) {
        if (!status.podeIrPara(novo)) {
            String dica = status.isFinal() ? " Para atender novamente é necessário um novo agendamento." : "";
            throw new RegraDeNegocioException("Não é possível mudar a consulta de '"
                    + status.getDescricao() + "' para '" + novo.getDescricao() + "'." + dica);
        }
        status = novo;
    }

    // ---------- Implementação comum dos métodos abstratos ----------

    @Override
    public boolean ocupaAgenda() {
        return status != StatusConsulta.CANCELADA && status != StatusConsulta.NAO_COMPARECEU;
    }

    @Override
    public String getSituacao() {
        return status.getDescricao();
    }

    // ---------- Getters e Setters ----------

    public StatusConsulta getStatus() {
        return status;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = exigirTexto(motivo, "motivo da consulta");
    }
}