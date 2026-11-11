package trabalhoPratico.sistemaHospitalar.model;

import static trabalhoPratico.sistemaHospitalar.util.Validacao.exigir;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.*;
import trabalhoPratico.sistemaHospitalar.exception.RegraDeNegocioException;
import trabalhoPratico.sistemaHospitalar.model.enums.TipoRegistro;

// Superclasse das 4 modalidades (consulta de rotina, consulta especializada, emergência e procedimento cirúrgico).
// Aqui ficam os dados e comportamentos COMUNS, para não repetir em cada modalidade.
// O que muda em cada uma é implementado nos métodos abstratos validarCondicoes() e ocupaAgenda().
@Entity
@Table(name = "atendimentos")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "modalidade", length = 30)
public abstract class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    // Pode ser nulo só na emergência que ainda aguarda avaliação
    @ManyToOne
    @JoinColumn(name = "profissional_id")
    private ProfissionalSaude profissional;

    // Data/horário de chegada, na emergência
    @Column(name = "data_atendimento", nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime horario;

    @Column(length = 2000)
    private String observacoes;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @OneToMany(mappedBy = "atendimento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistroClinico> registros = new ArrayList<>();

    protected Atendimento() {
    }

    protected Atendimento(Paciente paciente, ProfissionalSaude profissional,
                          LocalDate data, LocalTime horario) {
        this.paciente = exigir(paciente, "paciente");
        this.profissional = profissional;
        this.data = exigir(data, "data");
        this.horario = exigir(horario, "horário");
    }

    // ---------- O que cada modalidade precisa implementar ----------

    // Cada modalidade tem suas próprias condições e deve lançar RegraDeNegocioException se algo não for válido.
    public abstract void validarCondicoes();

    // Este atendimento ainda "ocupa" o horário do profissional? (cancelado não ocupa)
    public abstract boolean ocupaAgenda();

    // Texto da situação atual para mostrar na tela
    public abstract String getSituacao();

    // ---------- Comportamento comum ----------

    // Chamado por cada modalidade no FIM do seu construtor e valida tudo e só então liga o atendimento ao paciente e ao profissional.
    // Resultado: um atendimento inválido nunca chega a existir.
    protected final void finalizarCriacao() {
        validarAgendamento();
        paciente.adicionarAtendimento(this);
        if (profissional != null) {
            profissional.adicionarAtendimento(this);
        }
    }

    public final void validarAgendamento() {
        if (data.isBefore(LocalDate.now())) {
            throw new RegraDeNegocioException("Não é possível agendar para uma data passada.");
        }
        validarCondicoes();
    }

    // Verificações que quase todas as modalidades usam
    protected final void exigirProfissionalDisponivel(boolean exigirExpediente) {
        exigir(profissional, "profissional responsável");
        if (!profissional.isAtivo()) {
            throw new RegraDeNegocioException("O profissional " + profissional.getNome() + " está inativo.");
        }
        if (profissional.temConflitoEm(data, horario, this)) {
            throw new RegraDeNegocioException("O profissional " + profissional.getNome()
                    + " já possui outro atendimento em " + data + " às " + horario + ".");
        }
        if (exigirExpediente && !profissional.temExpedienteEm(data, horario)) {
            throw new RegraDeNegocioException("O profissional " + profissional.getNome()
                    + " não atende em " + data + " às " + horario + ".");
        }
    }

    // Dois atendimentos conflitam se são do mesmo profissional, no mesmo dia e horário
    public boolean conflitaCom(Atendimento outro) {
        return outro != null
                && !mesmoQue(outro)
                && profissional != null
                && profissional.equals(outro.profissional)
                && ocupaAgenda() && outro.ocupaAgenda()
                && data.equals(outro.data)
                && horario.equals(outro.horario);
    }

    protected final void alterarDataHorario(LocalDate novaData, LocalTime novoHorario) {
        this.data = exigir(novaData, "data");
        this.horario = exigir(novoHorario, "horário");
    }

    // Usado pela emergência, que começa sem profissional
    protected final void atribuirProfissional(ProfissionalSaude novo) {
        this.profissional = exigir(novo, "profissional responsável");
        novo.adicionarAtendimento(this);
    }

    // Cria um registro clínico dentro deste atendimento
    public RegistroClinico registrar(TipoRegistro tipo, String descricao) {
        RegistroClinico registro = new RegistroClinico(this, tipo, descricao);
        registros.add(registro);
        return registro;
    }

    // Compara por id (ou pela própria referência, se ainda não foi salvo)
    boolean mesmoQue(Atendimento outro) {
        if (outro == null) {
            return false;
        }
        return this == outro || (id != null && id.equals(outro.id));
    }

    // ---------- Getters e Setters ----------

    public Long getId() {
        return id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public ProfissionalSaude getProfissional() {
        return profissional;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public List<RegistroClinico> getRegistros() {
        return Collections.unmodifiableList(registros);
    }
}