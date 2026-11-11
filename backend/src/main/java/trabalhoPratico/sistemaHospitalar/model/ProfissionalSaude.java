package trabalhoPratico.sistemaHospitalar.model;

import static trabalhoPratico.sistemaHospitalar.util.Validacao.exigir;
import static trabalhoPratico.sistemaHospitalar.util.Validacao.exigirTexto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.*;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;
import trabalhoPratico.sistemaHospitalar.model.enums.Especialidade;

// Classe abstrata: nunca existe "um profissional genérico", sempre uma categoria
// (clínico geral, especialista ou cirurgião).
// SINGLE_TABLE = todas as categorias ficam na MESMA tabela, e a coluna "categoria"
// diz de qual tipo é cada linha.
@Entity
@Table(name = "profissionais_saude")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "categoria", length = 30)
public abstract class ProfissionalSaude extends Pessoa {

    @Column(name = "registro_profissional", nullable = false, unique = true, length = 30)
    private String registroProfissional;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Especialidade especialidade;

    @Column(nullable = false)
    private boolean ativo = true;

    @OneToMany(mappedBy = "profissional", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DisponibilidadeAtendimento> disponibilidades = new ArrayList<>();

    @OneToMany(mappedBy = "profissional")
    private List<Atendimento> atendimentos = new ArrayList<>();

    protected ProfissionalSaude() {
    }

    protected ProfissionalSaude(String nome, String telefone, String email,
                                String registroProfissional, Especialidade especialidade) {
        super(nome, telefone, email);
        this.registroProfissional = exigirTexto(registroProfissional, "registro profissional");
        this.especialidade = exigir(especialidade, "especialidade");
    }

    // ---------- Disponibilidade ----------

    // Existe algum expediente cadastrado que cubra essa data e hora?
    public boolean temExpedienteEm(LocalDate data, LocalTime hora) {
        return disponibilidades.stream().anyMatch(d -> d.cobre(data, hora));
    }

    // Regra 3: já existe OUTRO atendimento (que ocupa a agenda) nesse mesmo horário?
    // "ignorar" serve para o atendimento não conflitar consigo mesmo.
    public boolean temConflitoEm(LocalDate data, LocalTime hora, Atendimento ignorar) {
        return atendimentos.stream().anyMatch(a ->
                !a.mesmoQue(ignorar)
                        && a.ocupaAgenda()
                        && a.getData().equals(data)
                        && a.getHorario().equals(hora));
    }

    // Método do diagrama: está ativo, tem expediente e não tem outro atendimento no horário
    public boolean disponivelEm(LocalDate data, LocalTime hora) {
        return ativo && temExpedienteEm(data, hora) && !temConflitoEm(data, hora, null);
    }

    public DisponibilidadeAtendimento adicionarDisponibilidade(DayOfWeek dia, LocalTime inicio,
                                                               LocalTime fim, int duracaoConsultaMin) {
        DisponibilidadeAtendimento nova =
                new DisponibilidadeAtendimento(this, dia, inicio, fim, duracaoConsultaMin);
        for (DisponibilidadeAtendimento existente : disponibilidades) {
            if (existente.sobrepoe(dia, inicio, fim)) {
                throw new RegraDeNegocioException("Já existe um expediente que se sobrepõe a esse horário.");
            }
        }
        disponibilidades.add(nova);
        return nova;
    }

    public void removerDisponibilidade(DisponibilidadeAtendimento disponibilidade) {
        disponibilidades.remove(disponibilidade);
    }

    void adicionarAtendimento(Atendimento atendimento) {
        if (!atendimentos.contains(atendimento)) {
            atendimentos.add(atendimento);
        }
    }

    // ---------- Getters e Setters ----------

    public String getRegistroProfissional() {
        return registroProfissional;
    }

    public void setRegistroProfissional(String registroProfissional) {
        this.registroProfissional = exigirTexto(registroProfissional, "registro profissional");
    }

    public Especialidade getEspecialidade() {
        return especialidade;
    }

    // protected: cada categoria controla quais especialidades aceita
    protected void definirEspecialidade(Especialidade especialidade) {
        this.especialidade = exigir(especialidade, "especialidade");
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public List<DisponibilidadeAtendimento> getDisponibilidades() {
        return Collections.unmodifiableList(disponibilidades);
    }

    public List<Atendimento> getAtendimentos() {
        return Collections.unmodifiableList(atendimentos);
    }
}