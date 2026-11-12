package trabalhoPratico.sistemaHospitalar.model;

import static trabalhoPratico.sistemaHospitalar.util.Validacao.exigir;
import static trabalhoPratico.sistemaHospitalar.util.Validacao.exigirTexto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.*;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;
import trabalhoPratico.sistemaHospitalar.model.enums.PrioridadeEmergencia;
import trabalhoPratico.sistemaHospitalar.model.enums.StatusEmergencia;

// Exige avaliação imediata. Nasce SEM profissional (aguardando avaliação) eo profissional é definido quando alguém inicia a avaliação.
// Decisão: emergência fura a agenda, então não exige expediente e não "ocupa" horário
@Entity
@DiscriminatorValue("EMERGENCIA")
public class AtendimentoEmergencia extends Atendimento {

    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    private PrioridadeEmergencia prioridade;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_emergencia", length = 25)
    private StatusEmergencia status = StatusEmergencia.AGUARDANDO_AVALIACAO;

    @Column(name = "queixa_principal", length = 500)
    private String queixaPrincipal;

    protected AtendimentoEmergencia() {
    }

    public AtendimentoEmergencia(Paciente paciente, PrioridadeEmergencia prioridade, String queixaPrincipal) {
        // data/hora = momento da chegada
        super(paciente, null, LocalDate.now(), LocalTime.now().truncatedTo(ChronoUnit.MINUTES));
        this.prioridade = exigir(prioridade, "prioridade");
        this.queixaPrincipal = exigirTexto(queixaPrincipal, "queixa principal");
        finalizarCriacao();
    }

    // ---------- Ações ----------

    public void iniciarAvaliacao(ProfissionalSaude profissional) {
        exigir(profissional, "profissional responsável");
        if (!profissional.isAtivo()) {
            throw new RegraDeNegocioException("O profissional " + profissional.getNome() + " está inativo.");
        }
        transitar(StatusEmergencia.EM_ATENDIMENTO);
        atribuirProfissional(profissional);
    }

    public void concluir(String observacoes) {
        transitar(StatusEmergencia.CONCLUIDO);
        if (observacoes != null && !observacoes.isBlank()) {
            setObservacoes(observacoes.trim());
        }
    }

    private void transitar(StatusEmergencia novo) {
        if (!status.podeIrPara(novo)) {
            throw new RegraDeNegocioException("Não é possível mudar o atendimento de '"
                    + status.getDescricao() + "' para '" + novo.getDescricao() + "'.");
        }
        status = novo;
    }

    // ---------- Identificar a situação (fila de espera) ----------

    public boolean isAguardandoAvaliacao() {
        return status == StatusEmergencia.AGUARDANDO_AVALIACAO;
    }

    public boolean isEmAtendimento() {
        return status == StatusEmergencia.EM_ATENDIMENTO;
    }

    public boolean isConcluido() {
        return status == StatusEmergencia.CONCLUIDO;
    }

    // ---------- Métodos abstratos ----------

    @Override
    public void validarCondicoes() {
        exigir(prioridade, "prioridade");
        if (getProfissional() != null && !getProfissional().isAtivo()) {
            throw new RegraDeNegocioException("O profissional responsável está inativo.");
        }
    }

    @Override
    public boolean ocupaAgenda() {
        return false;
    }

    @Override
    public String getSituacao() {
        return status.getDescricao();
    }

    // ---------- Getters e Setters ----------

    public PrioridadeEmergencia getPrioridade() {
        return prioridade;
    }

    // Reclassificação (triagem): permitida enquanto não estiver concluído
    public void setPrioridade(PrioridadeEmergencia prioridade) {
        if (status == StatusEmergencia.CONCLUIDO) {
            throw new RegraDeNegocioException("Não é possível reclassificar um atendimento concluído.");
        }
        this.prioridade = exigir(prioridade, "prioridade");
    }

    public StatusEmergencia getStatus() {
        return status;
    }

    public String getQueixaPrincipal() {
        return queixaPrincipal;
    }

    public void setQueixaPrincipal(String queixaPrincipal) {
        this.queixaPrincipal = exigirTexto(queixaPrincipal, "queixa principal");
    }
}