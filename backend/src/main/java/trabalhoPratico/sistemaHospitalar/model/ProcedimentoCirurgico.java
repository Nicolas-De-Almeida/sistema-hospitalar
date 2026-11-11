package trabalhoPratico.sistemaHospitalar.model;

import static trabalhoPratico.sistemaHospitalar.util.Validacao.exigir;
import static trabalhoPratico.sistemaHospitalar.util.Validacao.exigirTexto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.*;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusProcedimento;
import trabalhoPratico.sistemaHospitalar.model.enums.TipoCirurgia;

// Cirurgia agendada ou realizada. data/horario herdados do Atendimento = PREVISTOS;
// dataEfetiva/horarioEfetivo = quando começou.
@Entity
@DiscriminatorValue("PROCEDIMENTO_CIRURGICO")
public class ProcedimentoCirurgico extends Atendimento {

    @Column(length = 1000)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cirurgia", length = 20)
    private TipoCirurgia tipoCirurgia;

    @Column(name = "observacoes_pre_operatorias", length = 2000)
    private String observacoesPreOperatorias;

    @Column(name = "observacoes_pos_operatorias", length = 2000)
    private String observacoesPosOperatorias;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_procedimento", length = 20)
    private StatusProcedimento status = StatusProcedimento.AGENDADO;

    @Column(name = "data_efetiva")
    private LocalDate dataEfetiva;

    @Column(name = "horario_efetivo")
    private LocalTime horarioEfetivo;

    protected ProcedimentoCirurgico() {
    }

    public ProcedimentoCirurgico(Paciente paciente, Cirurgiao cirurgiao,
                                 LocalDate dataPrevista, LocalTime horarioPrevisto,
                                 String descricao, TipoCirurgia tipoCirurgia,
                                 String observacoesPreOperatorias) {
        super(paciente, exigir(cirurgiao, "cirurgião responsável"), dataPrevista, horarioPrevisto);
        this.descricao = exigirTexto(descricao, "descrição do procedimento");
        this.tipoCirurgia = exigir(tipoCirurgia, "tipo de cirurgia");
        this.observacoesPreOperatorias = observacoesPreOperatorias;
        finalizarCriacao();
    }

    // ---------- Ações ----------

    // Começa a cirurgia: registra data/hora efetivas
    public void iniciar() {
        validarCondicoes(); 
        transitar(StatusProcedimento.EM_ANDAMENTO);
        this.dataEfetiva = LocalDate.now();
        this.horarioEfetivo = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
    }

    // Só conclui quem foi iniciado
    public void concluir(String observacoesPosOperatorias) {
        if (status == StatusProcedimento.AGENDADO) {
            throw new RegraDeNegocioException(
                    "Não é permitido concluir um procedimento que ainda não foi iniciado.");
        }
        transitar(StatusProcedimento.CONCLUIDO);
        if (observacoesPosOperatorias != null && !observacoesPosOperatorias.isBlank()) {
            this.observacoesPosOperatorias = observacoesPosOperatorias.trim();
        }
    }

    public void cancelar() {
        transitar(StatusProcedimento.CANCELADO);
    }

    private void transitar(StatusProcedimento novo) {
        if (!status.podeIrPara(novo)) {
            throw new RegraDeNegocioException("Não é possível mudar o procedimento de '"
                    + status.getDescricao() + "' para '" + novo.getDescricao() + "'.");
        }
        status = novo;
    }

    // ---------- Métodos abstratos ----------

    @Override
    public void validarCondicoes() {
        if (!(getProfissional() instanceof Cirurgiao)) {
            throw new RegraDeNegocioException("O procedimento cirúrgico deve ter um cirurgião responsável.");
        }
        exigirTexto(descricao, "descrição do procedimento");
        exigir(tipoCirurgia, "tipo de cirurgia");
        exigirProfissionalDisponivel(true);
    }

    @Override
    public boolean ocupaAgenda() {
        return status != StatusProcedimento.CANCELADO;
    }

    @Override
    public String getSituacao() {
        return status.getDescricao();
    }

    // ---------- Getters e Setters ----------

    public Cirurgiao getCirurgiao() {
        return (Cirurgiao) getProfissional();
    }

    public LocalDate getDataPrevista() {
        return getData();
    }

    public LocalTime getHorarioPrevisto() {
        return getHorario();
    }

    public LocalDate getDataEfetiva() {
        return dataEfetiva;
    }

    public LocalTime getHorarioEfetivo() {
        return horarioEfetivo;
    }

    public StatusProcedimento getStatus() {
        return status;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = exigirTexto(descricao, "descrição do procedimento");
    }

    public TipoCirurgia getTipoCirurgia() {
        return tipoCirurgia;
    }

    public void setTipoCirurgia(TipoCirurgia tipoCirurgia) {
        this.tipoCirurgia = exigir(tipoCirurgia, "tipo de cirurgia");
    }

    public String getObservacoesPreOperatorias() {
        return observacoesPreOperatorias;
    }

    public void setObservacoesPreOperatorias(String observacoesPreOperatorias) {
        this.observacoesPreOperatorias = observacoesPreOperatorias;
    }

    public String getObservacoesPosOperatorias() {
        return observacoesPosOperatorias;
    }

    public void setObservacoesPosOperatorias(String observacoesPosOperatorias) {
        this.observacoesPosOperatorias = observacoesPosOperatorias;
    }
}